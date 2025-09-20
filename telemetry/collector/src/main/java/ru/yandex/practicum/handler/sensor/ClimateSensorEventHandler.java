package ru.yandex.practicum.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
public class ClimateSensorEventHandler extends SensorEventHandlerImpl<ClimateSensorAvro> {

    public ClimateSensorEventHandler(EventProducer eventProducer) {
        super(eventProducer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        eventProducer.sendSensorEvent(mapToSensorEventAvro(event));
    }

    @Override
    protected ClimateSensorAvro mapToAvro(SensorEventProto event) {
        ClimateSensorEventProto climateSensorEvent = event.getClimateSensorEvent();
        return ClimateSensorAvro.newBuilder()
                .setCo2Level(climateSensorEvent.getCo2Level())
                .setHumidity(climateSensorEvent.getHumidity())
                .setTemperatureC(climateSensorEvent.getTemperatureC())
                .build();
    }
}
