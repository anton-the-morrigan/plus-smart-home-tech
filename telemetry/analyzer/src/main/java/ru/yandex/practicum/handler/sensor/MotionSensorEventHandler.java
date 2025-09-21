package ru.yandex.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Slf4j
@Component
public class MotionSensorEventHandler implements SensorEventHandler{
    @Override
    public String getType() {
        return MotionSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType type, SensorStateAvro sensorStateAvro) {
        log.info("MotionSensorEventHandler getSensorValue");
        MotionSensorAvro motionSensorAvro = (MotionSensorAvro) sensorStateAvro.getData();
        return motionSensorAvro.getMotion() ? 1 : 0;
    }
}
