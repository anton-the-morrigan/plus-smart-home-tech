package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.delivery.client.DeliveryClient;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.dto.DeliveryState;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.OrderState;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.order.exception.NoOrderFoundException;
import ru.yandex.practicum.payment.client.PaymentClient;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.client.WarehouseClient;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;

    @Override
    public List<OrderDto> getOrders(String username) {
        List<Order> orders = orderRepository.findByUsername(username);
        List<OrderDto> ordersDTO = new ArrayList<>();
        for (Order order : orders) {
            OrderDto orderDto = orderMapper.toOrderDto(order);
            ordersDTO.add(orderDto);
        }
        return ordersDTO;
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        BookedProductsDto bookedProductsDto = warehouseClient.checkShoppingCart(request.getShoppingCart());
        Order order = orderMapper.toOrder(request, bookedProductsDto);

        AddressDto addressDto = warehouseClient.getWarehouseAddress();
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setFromAddress(addressDto);
        deliveryDto.setToAddress(request.getDeliveryAddress());
        deliveryDto.setOrderId(order.getOrderId());
        deliveryDto.setDeliveryState(DeliveryState.CREATED);
        deliveryDto = deliveryClient.createDelivery(deliveryDto);
        order.setDeliveryId(deliveryDto.getDeliveryId());

        order = orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", request.getOrderId())));
        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto paymentSuccess(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        order.setState(OrderState.PAID);
        order = orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        order.setState(OrderState.PAYMENT_FAILED);
        order = orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliverySuccess(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto completed(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        order.setState(OrderState.COMPLETED);
        order = orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        double productPrice = paymentClient.getProductCost(orderMapper.toOrderDto(order));
        order.setProductPrice(productPrice);
        order = orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        double deliveryPrice = deliveryClient.calculateCost(orderMapper.toOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);
        order = orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblySuccess(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        order.setState(OrderState.ASSEMBLED);
        order = orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с id %s не найден", orderId)));
        order.setState(OrderState.ASSEMBLY_FAILED);
        order = orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }
}
