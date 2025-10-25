package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.store.dto.ProductCategory;

import java.util.UUID;

public interface ShoppingStoreRepository  extends JpaRepository<Product, UUID> {
    Page<Product> findByProductCategory(ProductCategory productCategory, Pageable pageable);
}