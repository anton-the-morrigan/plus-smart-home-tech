package ru.yandex.practicum.store.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.store.dto.ProductCategory;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) throws FeignException;

    @PutMapping
    ProductDto createNewProduct(@RequestBody ProductDto productDto) throws FeignException;

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto product) throws FeignException;

    @PostMapping("/removeProductFromStore")
    void removeProductFromStore(@RequestBody UUID productId) throws FeignException;

    @PostMapping("/quantityState")
    void setProductQuantityState(@RequestParam UUID productId,
                                 @RequestParam QuantityState quantityState) throws FeignException;

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId) throws FeignException;
}
