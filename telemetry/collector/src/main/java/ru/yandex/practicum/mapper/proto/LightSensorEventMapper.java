package ru.yandex.practicum.mapper.proto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

import java.time.Instant;

@Component
public class LightSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public SensorEvent map(SensorEventProto event) {
        LightSensorEventProto sensorEvent = event.getLightSensorEvent();

        return LightSensorEvent.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .linkQuality(sensorEvent.getLinkQuality())
                .luminosity(sensorEvent.getLuminosity())
                .build();
    }
}
