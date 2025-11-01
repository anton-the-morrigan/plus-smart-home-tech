package ru.yandex.practicum.warehouse.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

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
}
