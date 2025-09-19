package ru.yandex.practicum.mapper.proto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

import java.time.Instant;

@Component
public class MotionSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public SensorEvent map(SensorEventProto event) {
        MotionSensorEventProto sensorEvent = event.getMotionSensorEvent();

        return MotionSensorEvent.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .linkQuality(sensorEvent.getLinkQuality())
                .motion(sensorEvent.getMotion())
                .voltage(sensorEvent.getVoltage())
                .build();
    }
}
