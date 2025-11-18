package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.dto.PaymentState;
import ru.yandex.practicum.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.store.client.ShoppingStoreClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;

    private final static double VAT = 0.1;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        validate(orderDto);
        Payment payment = paymentMapper.toPayment(orderDto);
        paymentRepository.save(payment);
        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    public Double getTotalCost(OrderDto orderDto) {
        validate(orderDto);
        double productsPrice = getProductCost(orderDto);
        double deliveryPrice = orderDto.getDeliveryPrice();
        return deliveryPrice + productsPrice + (productsPrice * VAT);
    }

    @Override
    public void refundPayment(UUID paymentId) {
        Payment payment = findPayment(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

    }

    @Override
    public Double getProductCost(OrderDto orderDto) {
        double totalPrice = 0.0;

        validate(orderDto);
        Map<UUID, Long> products = orderDto.getProducts();

        Set<UUID> productIds = products.keySet();
        for (UUID id : productIds) {
            Long quantity = products.get(id);
            totalPrice += quantity * shoppingStoreClient.getProduct(id).getPrice();
        }

        return totalPrice;
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        Payment payment = findPayment(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        paymentRepository.save(payment);
    }

    private void validate(OrderDto orderDto) {
        List<Double> prices = new ArrayList<>();
        prices.add(orderDto.getTotalPrice());
        prices.add(orderDto.getDeliveryPrice());
        prices.add(orderDto.getProductPrice());

        for (Double price : prices) {
            if (price == null || price == 0) {
                throw new NotEnoughInfoInOrderToCalculateException(String.format("В заказе с id %s нет необходимой информации", orderDto.getOrderId()));
            }
        }
    }

    private Payment findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() ->
                new NoPaymentFoundException(String.format("Платёж с id %s не найден", paymentId)));
    }
}
