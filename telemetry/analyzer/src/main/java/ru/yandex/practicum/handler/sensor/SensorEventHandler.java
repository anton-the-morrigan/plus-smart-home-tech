package ru.yandex.practicum.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

public interface SensorEventHandler {
    String getType();

    Integer getSensorValue(ConditionType type, SensorStateAvro sensorStateAvro);
}
