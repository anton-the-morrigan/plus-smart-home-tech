package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.client.WarehouseClient;
import ru.yandex.practicum.warehouse.dto.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {
    private final WarehouseService warehouseService;

    @Override
    public void addProductToWarehouse(NewProductInWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkShoppingCart(shoppingCartDto);
    }

    @Override
    public void increaseProductQuantity(AddProductToWarehouseRequest request) {
        warehouseService.increaseProductQuantity(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        warehouseService.shippedToDelivery(request);
    }

    @Override
    public void returnProducts(Map<UUID, Long> returnedProducts) {
        warehouseService.returnProducts(returnedProducts);
    }

    @Override
    public BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request) {
        return warehouseService.assembleProducts(request);
    }
}
