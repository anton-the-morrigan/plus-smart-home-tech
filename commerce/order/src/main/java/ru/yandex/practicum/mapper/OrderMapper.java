package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.OrderState;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;

@Component
public class OrderMapper {

    public Order toOrder(CreateNewOrderRequest request, BookedProductsDto bookedProductsDto) {
        return new Order(
                null,
                null,
                request.getShoppingCart().getShoppingCartId(),
                request.getShoppingCart().getProducts(),
                null,
                null,
                OrderState.NEW,
                bookedProductsDto.getDeliveryWeight(),
                bookedProductsDto.getDeliveryVolume(),
                bookedProductsDto.getFragile(),
                null,
                null,
                null
        );
    }

    public OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getOrderId(),
                order.getUsername(),
                order.getShoppingCartId(),
                order.getProducts(),
                order.getPaymentId(),
                order.getDeliveryId(),
                order.getState(),
                order.getDeliveryWeight(),
                order.getDeliveryVolume(),
                order.getFragile(),
                order.getTotalPrice(),
                order.getDeliveryPrice(),
                order.getProductPrice()
        );
    }
}
