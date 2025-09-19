package ru.yandex.practicum.mapper.proto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

import java.time.Instant;

@Component
public class ClimateSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public SensorEvent map(SensorEventProto event) {
        ClimateSensorEventProto sensorEvent = event.getClimateSensorEvent();

        return ClimateSensorEvent.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .temperatureC(sensorEvent.getTemperatureC())
                .co2Level(sensorEvent.getCo2Level())
                .humidity(sensorEvent.getHumidity())
                .build();
    }
}
