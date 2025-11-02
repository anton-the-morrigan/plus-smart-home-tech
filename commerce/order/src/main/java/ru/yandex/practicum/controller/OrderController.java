package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.client.OrderClient;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderClient {
    private final OrderService orderService;

    @Override
    public List<OrderDto> getOrders(String username) {
        return orderService.getOrders(username);
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        return orderService.createOrder(request);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        return orderService.returnOrder(request);
    }

    @Override
    public OrderDto paymentSuccess(UUID orderId) {
        return orderService.paymentSuccess(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto deliverySuccess(UUID orderId) {
        return orderService.deliverySuccess(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto completed(UUID orderId) {
        return orderService.completed(orderId);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        return orderService.calculateTotal(orderId);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        return orderService.calculateDelivery(orderId);
    }

    @Override
    public OrderDto assemblySuccess(UUID orderId) {
        return orderService.assemblySuccess(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        return orderService.assemblyFailed(orderId);
    }
}