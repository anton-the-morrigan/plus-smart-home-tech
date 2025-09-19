package ru.yandex.practicum.mapper.proto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorEventProto;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

import java.time.Instant;

@Component
public class TemperatureSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public SensorEvent map(SensorEventProto event) {
        TemperatureSensorEventProto sensorEvent = event.getTemperatureSensorEvent();

        return TemperatureSensorEvent.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .temperatureC(sensorEvent.getTemperatureC())
                .temperatureF(sensorEvent.getTemperatureF())
                .build();
    }
}
