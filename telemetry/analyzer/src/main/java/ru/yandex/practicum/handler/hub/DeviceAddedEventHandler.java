package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {
        sensorRepository.save(toSensor(hubEventAvro));
    }

    private Sensor toSensor(HubEventAvro hubEventAvro) {
        DeviceAddedEventAvro device = (DeviceAddedEventAvro) hubEventAvro.getPayload();
        return Sensor.builder().id(device.getId()).hubId(hubEventAvro.getHubId()).build();
    }
}
