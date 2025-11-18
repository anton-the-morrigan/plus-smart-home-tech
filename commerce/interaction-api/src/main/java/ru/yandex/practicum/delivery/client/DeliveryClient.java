package ru.yandex.practicum.delivery.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;

import java.util.UUID;

@FeignClient(name ="delivery")
public interface DeliveryClient {

    @PutMapping()
    DeliveryDto createDelivery(@RequestBody DeliveryDto deliveryDto) throws FeignException;

    @PostMapping("/successful")
    void successful(@RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/picked")
    void picked(@RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/failed")
    void failed(@RequestBody UUID deliveryId) throws FeignException;

    @PostMapping("/cost")
    Double calculateCost(@RequestBody OrderDto orderDto) throws FeignException;
}
