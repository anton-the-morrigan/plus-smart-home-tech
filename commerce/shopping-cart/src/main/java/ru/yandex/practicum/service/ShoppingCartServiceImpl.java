package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartState;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.warehouse.client.WarehouseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart shoppingCart = getCartOrCreateANewOne(username);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addToCart(String username, Map<UUID, Long> products) {
        ShoppingCart shoppingCart = getCartOrCreateANewOne(username);
        Map<UUID, Long> oldProducts = shoppingCart.getProducts();
        oldProducts.putAll(products);
        shoppingCart.setProducts(oldProducts);
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDto(shoppingCart);
        warehouseClient.checkShoppingCart(shoppingCartDto);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartDto;
    }

    @Override
    @Transactional
    public void deleteCart(String username) {
        ShoppingCart shoppingCart = getCartOrCreateANewOne(username);
        shoppingCart.setState(ShoppingCartState.DEACTIVATE);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromCart(String username, List<UUID> products) {
        ShoppingCart shoppingCart = getCartOrCreateANewOne(username);
        Map<UUID, Long> oldProducts = shoppingCart.getProducts();
        products.forEach(oldProducts::remove);
        shoppingCart.setProducts(oldProducts);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart shoppingCart = getCartOrCreateANewOne(username);
        Map<UUID, Long> oldProducts = shoppingCart.getProducts();
        oldProducts.put(request.getProductId(), request.getNewQuantity());
        shoppingCart.setProducts(oldProducts);
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDto(shoppingCart);
        warehouseClient.checkShoppingCart(shoppingCartDto);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartDto;
    }

    private ShoppingCart getCartOrCreateANewOne(String username) {
        return shoppingCartRepository.findByUsernameAndState(username, ShoppingCartState.ACTIVE)
                .orElseGet(() -> {
                    ShoppingCart newShoppingCart = new ShoppingCart();
                    newShoppingCart.setUsername(username);
                    newShoppingCart.setProducts(new HashMap<>());
                    newShoppingCart.setState(ShoppingCartState.ACTIVE);
                    return shoppingCartRepository.save(newShoppingCart);
                });
    }
}
