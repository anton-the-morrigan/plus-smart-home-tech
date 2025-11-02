package ru.yandex.practicum.payment.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.util.UUID;

@FeignClient(name ="payment")
public interface PaymentClient {

    @PostMapping()
    PaymentDto createPayment(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/totalCost")
    Double getTotalCost(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/refund")
    void refundPayment(UUID paymentId) throws FeignException;

    @PostMapping("/productCost")
    Double getProductCost(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID paymentId) throws FeignException;
}
