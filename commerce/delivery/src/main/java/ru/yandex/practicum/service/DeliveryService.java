package ru.yandex.practicum.service;

import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    void successful(UUID deliveryId);

    void picked(UUID deliveryId);

    void failed(UUID deliveryId);

    Double calculateCost(OrderDto orderDto);
}
