package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addToCart(String username, Map<UUID, Long> products);

    void deleteCart(String username);

    ShoppingCartDto removeFromCart(String username, List<UUID> products);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
