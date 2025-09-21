package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Component
@RequiredArgsConstructor
public class DeviceRemoveEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getType() {
        return DeviceRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {
        String sensorId = ((DeviceRemovedEventAvro) hubEventAvro.getPayload()).getId();
        sensorRepository.deleteByIdAndHubId(sensorId, hubEventAvro.getHubId());
    }
}
