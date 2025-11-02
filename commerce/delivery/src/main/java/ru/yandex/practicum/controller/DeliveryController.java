package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.client.DeliveryClient;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryClient {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        return deliveryService.createDelivery(deliveryDto);
    }

    @Override
    public void successful(UUID deliveryId) {
        deliveryService.successful(deliveryId);
    }

    @Override
    public void picked(UUID deliveryId) {
        deliveryService.picked(deliveryId);
    }

    @Override
    public void failed(UUID deliveryId) {
        deliveryService.failed(deliveryId);
    }

    @Override
    public Double calculateCost(OrderDto orderDto) {
        return deliveryService.calculateCost(orderDto);
    }
}
