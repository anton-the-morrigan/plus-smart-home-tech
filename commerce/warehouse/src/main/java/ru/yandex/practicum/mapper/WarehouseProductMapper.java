package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

@Component
public class WarehouseProductMapper {

    public WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest request) {
        return new WarehouseProduct(
                request.getProductId(),
                request.getFragile(),
                request.getDimension().getWidth(),
                request.getDimension().getHeight(),
                request.getDimension().getDepth(),
                request.getWeight(),
                0L
        );
    }
}
