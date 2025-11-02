package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.model.Delivery;

@Component
public class DeliveryMapper {

    public Delivery toDelivery(DeliveryDto deliveryDto) {
        return new Delivery(
                deliveryDto.getDeliveryId(),
                deliveryDto.getFromAddress(),
                deliveryDto.getToAddress(),
                deliveryDto.getOrderId(),
                deliveryDto.getDeliveryState()
        );
    }

    public DeliveryDto toDeliveryDto(Delivery delivery) {
        return new DeliveryDto(
                delivery.getDeliveryId(),
                delivery.getFromAddress(),
                delivery.getToAddress(),
                delivery.getOrderId(),
                delivery.getDeliveryState()
        );
    }
}
