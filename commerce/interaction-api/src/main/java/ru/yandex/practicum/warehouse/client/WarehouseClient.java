package ru.yandex.practicum.warehouse.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @PutMapping
    void addProductToWarehouse(@RequestBody NewProductInWarehouseRequest request) throws FeignException;

    @PostMapping("/check")
    BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/add")
    void increaseProductQuantity(@RequestBody AddProductToWarehouseRequest request) throws FeignException;

    @GetMapping("/address")
    AddressDto getWarehouseAddress() throws FeignException;

    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request) throws FeignException;

    @PostMapping("/return")
    void returnProducts(@RequestBody Map<UUID, Long> returnedProducts) throws FeignException;

    @PostMapping("/assembly")
    BookedProductsDto assembleProducts(@RequestBody AssemblyProductsForOrderRequest request) throws FeignException;
}
