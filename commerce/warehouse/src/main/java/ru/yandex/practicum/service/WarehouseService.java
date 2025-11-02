package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void addProductToWarehouse(NewProductInWarehouseRequest request);

    BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto);

    void increaseProductQuantity(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void returnProducts(Map<UUID, Long> returnedProducts);

    BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request);
}
