package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart) {
        return new ShoppingCartDto(
            shoppingCart.getShoppingCartId(),
            shoppingCart.getProducts()
        );
    }
}
