package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.store.dto.ProductCategory;
import ru.yandex.practicum.store.dto.ProductState;
import ru.yandex.practicum.store.dto.QuantityState;

import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    UUID productId;

    @Column(name = "product_name")
    String productName;

    String description;

    String image_src;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "quantity_state")
    QuantityState quantityState;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_state")
    ProductState productState;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_category")
    ProductCategory productCategory;

    Double price;

}
