package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.store.client.ShoppingStoreClient;
import ru.yandex.practicum.store.dto.QuantityState;
import ru.yandex.practicum.store.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseProductMapper warehouseProductMapper;
    private final ShoppingStoreClient shoppingStoreClient;

    private static final AddressDto[] ADDRESSES =
            new AddressDto[]{
                    new AddressDto(
                            "country1",
                            "city1",
                            "street1",
                            "house1",
                            "flat1"),
                    new AddressDto(
                            "country2",
                            "city2",
                            "street2",
                            "house2",
                            "flat2")};

    @Override
    @Transactional
    public void addProductToWarehouse(NewProductInWarehouseRequest request) {
        warehouseRepository.findById(request.getProductId()).ifPresent(product -> {
                    throw new SpecifiedProductAlreadyInWarehouseException("Товар уже есть на складе");
                });
        WarehouseProduct product = warehouseProductMapper.toWarehouseProduct(request);
        warehouseRepository.save(product);
    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> cartProducts = shoppingCartDto.getProducts();
        Map<UUID, WarehouseProduct> products = warehouseRepository.findAllById(cartProducts.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        if (products.size() != cartProducts.size()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Некоторых товаров нет на складе");
        }

        Double weight = 0.0;
        Double volume = 0.0;
        Boolean fragile = false;

        for (Map.Entry<UUID, Long> cartProduct : cartProducts.entrySet()) {
            WarehouseProduct product = products.get(cartProduct.getKey());
            if (cartProduct.getValue() > product.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Товара на складе недостаточно");
            }
            weight += product.getWeight() * cartProduct.getValue();
            volume += product.getHeight() * product.getWeight() * product.getDepth() * cartProduct.getValue();
            fragile = fragile || product.getFragile();
        }

        return new BookedProductsDto(
                weight,
                volume,
                fragile
        );
    }

    @Override
    @Transactional
    public void increaseProductQuantity(AddProductToWarehouseRequest request) {
        WarehouseProduct product = warehouseRepository.findById(request.getProductId()).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("Товар не найден на складе")
        );
        Long quantity = product.getQuantity();
        quantity += request.getQuantity();
        product.setQuantity(quantity);
        warehouseRepository.save(product);
        //updateQuantityInShoppingStore(product);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];
    }

    private void updateQuantityInShoppingStore(WarehouseProduct product) {
        Long quantity = product.getQuantity();
        QuantityState quantityState;

        if (quantity == 0) {
            quantityState = QuantityState.ENDED;
        } else if (0 < quantity && quantity <= 10) {
            quantityState = QuantityState.FEW;
        } else if (10 < quantity && quantity <= 100) {
            quantityState = QuantityState.ENOUGH;
        } else {
            quantityState = QuantityState.MANY;
        }

        shoppingStoreClient.setProductQuantityState(product.getProductId(), quantityState);
    }
}
