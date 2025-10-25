package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.store.dto.ProductDto;

@Component
public class ProductMapper {

    public Product toProduct(ProductDto productDto) {
        return new Product(
                productDto.getProductId(),
                productDto.getProductName(),
                productDto.getDescription(),
                productDto.getImageSrc(),
                productDto.getQuantityState(),
                productDto.getProductState(),
                productDto.getProductCategory(),
                productDto.getPrice()
        );
    }

    public ProductDto toProductDto(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getImage_src(),
                product.getQuantityState(),
                product.getProductState(),
                product.getProductCategory(),
                product.getPrice()
        );
    }
}
