package ru.yandex.practicum.delivery.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.warehouse.dto.AddressDto;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {
    UUID deliveryId;
    AddressDto fromAddress;
    AddressDto toAddress;
    UUID orderId;
    DeliveryState deliveryState;
}
