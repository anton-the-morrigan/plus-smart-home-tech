package ru.yandex.practicum.order.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddressDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {
    ShoppingCartDto shoppingCart;
    AddressDto deliveryAddress;
}
