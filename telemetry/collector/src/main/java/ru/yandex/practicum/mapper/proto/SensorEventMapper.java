package ru.yandex.practicum.mapper.proto;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensor.SensorEvent;

public interface SensorEventMapper {
    SensorEventProto.PayloadCase getMessageType();

    SensorEvent map(SensorEventProto event);
}
