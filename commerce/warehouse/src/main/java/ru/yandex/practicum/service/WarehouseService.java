package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

public interface WarehouseService {

    void addProductToWarehouse(NewProductInWarehouseRequest request);

    BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto);

    void increaseProductQuantity(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}
