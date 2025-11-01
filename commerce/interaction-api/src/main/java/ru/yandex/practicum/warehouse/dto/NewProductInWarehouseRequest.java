package ru.yandex.practicum.warehouse.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {
    UUID productId;
    Boolean fragile;
    DimensionDto dimension;
    Double weight;
}
