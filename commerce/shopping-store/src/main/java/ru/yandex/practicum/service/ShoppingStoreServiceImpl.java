package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;
import ru.yandex.practicum.store.dto.*;
import ru.yandex.practicum.store.exception.ProductNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ShoppingStoreRepository shoppingStoreRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products = shoppingStoreRepository.findByProductCategory(category, pageable);
        return products.map(productMapper::toProductDto);
    }

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);
        product = shoppingStoreRepository.save(product);
        return productMapper.toProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        shoppingStoreRepository.findById(productDto.getProductId()).orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
        Product newProduct = productMapper.toProduct(productDto);
        newProduct = shoppingStoreRepository.save(newProduct);
        return productMapper.toProductDto(newProduct);
    }

    @Override
    @Transactional
    public void removeProductFromStore(UUID productId) {
        Product product = shoppingStoreRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
        product.setProductState(ProductState.DEACTIVATE);
        shoppingStoreRepository.save(product);
    }

    @Override
    @Transactional
    public void setProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = shoppingStoreRepository.findById(request.getProductId()).orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
        product.setQuantityState(request.getQuantityState());
        shoppingStoreRepository.save(product);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Product product = shoppingStoreRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
        return productMapper.toProductDto(product);
    }
}
