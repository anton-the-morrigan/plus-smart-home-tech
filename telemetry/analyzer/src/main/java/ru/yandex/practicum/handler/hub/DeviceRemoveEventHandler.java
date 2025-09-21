package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j

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
        log.info("DeviceRemoveEventHandler handle");
        String sensorId = ((DeviceRemovedEventAvro) hubEventAvro.getPayload()).getId();
        sensorRepository.deleteByIdAndHubId(sensorId, hubEventAvro.getHubId());
    }
}
