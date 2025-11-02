package ru.yandex.practicum.order.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name ="order")
public interface OrderClient {

    @GetMapping()
    List<OrderDto> getOrders(@RequestParam String username) throws FeignException;

    @PutMapping
    OrderDto createOrder(@RequestBody CreateNewOrderRequest request) throws FeignException;

    @PostMapping("/return")
    OrderDto returnOrder(@RequestParam ProductReturnRequest request) throws FeignException;

    @PostMapping("/payment")
    OrderDto paymentSuccess(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery")
    OrderDto deliverySuccess(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/completed")
    OrderDto completed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/total")
    OrderDto calculateTotal(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/delivery")
    OrderDto calculateDelivery(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly")
    OrderDto assemblySuccess(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId) throws FeignException;
}
