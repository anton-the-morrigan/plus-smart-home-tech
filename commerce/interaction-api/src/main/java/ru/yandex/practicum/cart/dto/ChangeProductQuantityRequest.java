package ru.yandex.practicum.cart.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeProductQuantityRequest {
    UUID productId;
    Integer newQuantity;
}
