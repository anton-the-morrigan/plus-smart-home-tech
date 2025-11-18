package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.dto.DeliveryState;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.dto.AddressDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;

    private static final double BASE_PRICE = 5.0;
    private static final double WAREHOUSE_1_ADDRESS_MULTIPLIER = 1;
    private static final double WAREHOUSE_2_ADDRESS_MULTIPLIER = 2;
    private static final double FRAGILE_MULTIPLIER = 0.2;
    private static final double WEIGHT_MULTIPLIER = 0.3;
    private static final double VOLUME_MULTIPLIER = 0.2;
    private static final double STREET_MULTIPLIER = 0.2;

    @Override
    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);
        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDeliveryDto(delivery);
    }

    @Override
    @Transactional
    public void successful(UUID deliveryId) {
        Delivery delivery = findDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void picked(UUID deliveryId) {
        Delivery delivery = findDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void failed(UUID deliveryId) {
        Delivery delivery = findDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
    }

    @Override
    public Double calculateCost(OrderDto orderDto) {
        Delivery delivery = findDelivery(orderDto.getDeliveryId());

        AddressDto warehouseAddress = delivery.getFromAddress();
        AddressDto destinationAddress = delivery.getToAddress();

        double cost = BASE_PRICE;

        if (warehouseAddress.getStreet().equals("ADDRESS_1")) {
            cost = cost * WAREHOUSE_1_ADDRESS_MULTIPLIER;
        } else if (warehouseAddress.getStreet().equals("ADDRESS_2")) {
            cost = cost * WAREHOUSE_2_ADDRESS_MULTIPLIER;
        }

        if (orderDto.getFragile()) {
            cost += cost * FRAGILE_MULTIPLIER;
        }

        cost += orderDto.getDeliveryWeight() * WEIGHT_MULTIPLIER;

        cost += orderDto.getDeliveryVolume() * VOLUME_MULTIPLIER;

        if (!warehouseAddress.getStreet().equals(destinationAddress.getStreet())) {
            cost += cost * STREET_MULTIPLIER;
        }

        return cost;
    }

    private Delivery findDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow(() ->
                new NoDeliveryFoundException(String.format("Доставка с id %s не найдена", deliveryId)));
    }
}
