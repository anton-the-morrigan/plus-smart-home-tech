package ru.yandex.practicum.cart.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {
    UUID shoppingCartId;
    Map<UUID, Integer> products;
}
