package ru.yandex.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Slf4j
@Component
public class ClimateSensorEventHandler implements SensorEventHandler{

    @Override
    public String getType() {
        return ClimateSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType type, SensorStateAvro sensorStateAvro) {
        log.debug("ClimateSensorEventHandler getSensorValue");
        ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) sensorStateAvro.getData();
        return switch (type) {
            case ConditionType.TEMPERATURE -> climateSensorAvro.getTemperatureC();
            case ConditionType.HUMIDITY -> climateSensorAvro.getHumidity();
            case ConditionType.CO2LEVEL -> climateSensorAvro.getCo2Level();
            default -> null;
        };
    }
}
