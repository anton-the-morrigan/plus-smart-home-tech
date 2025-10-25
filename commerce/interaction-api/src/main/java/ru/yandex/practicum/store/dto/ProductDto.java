package ru.yandex.practicum.store.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    UUID productId;
    String productName;
    String description;
    String imageSrc;
    QuantityState quantityState;
    ProductState productState;
    ProductCategory productCategory;
    Double price;
}
