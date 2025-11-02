package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.dto.PaymentState;

@Component
public class PaymentMapper {

    public Payment toPayment(OrderDto orderDto) {
        return new Payment(
                orderDto.getPaymentId(),
                orderDto.getProductPrice(),
                orderDto.getDeliveryPrice(),
                orderDto.getTotalPrice(),
                PaymentState.PENDING
        );
    }

    public PaymentDto toPaymentDto(Payment payment) {
        return new PaymentDto(
                payment.getPaymentId(),
                payment.getTotalPayment(),
                payment.getDeliveryTotal(),
                payment.getFeeTotal(),
                payment.getPaymentState()
        );
    }
}
