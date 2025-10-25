package ru.yandex.practicum.cart.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username) throws FeignException;

    @PutMapping
    ShoppingCartDto addToCart(@RequestParam String username,
                              @RequestBody Map<UUID, Long> products) throws FeignException;

    @DeleteMapping
    void deleteCart(@RequestParam String username) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeFromCart(@RequestParam String username,
                                   @RequestBody List<UUID> products) throws FeignException;

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest request) throws FeignException;
}
