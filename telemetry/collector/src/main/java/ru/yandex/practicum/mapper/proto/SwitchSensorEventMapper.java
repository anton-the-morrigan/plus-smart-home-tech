package ru.yandex.practicum.mapper.proto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorEventProto;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;

import java.time.Instant;

@Component
public class SwitchSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public SensorEvent map(SensorEventProto event) {
        SwitchSensorEventProto sensorEvent = event.getSwitchSensorEvent();

        return SwitchSensorEvent.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .state(sensorEvent.getState())
                .build();
    }
}
