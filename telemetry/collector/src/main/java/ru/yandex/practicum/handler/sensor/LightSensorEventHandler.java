package ru.yandex.practicum.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler  extends SensorEventHandlerImpl<LightSensorAvro> {

    public LightSensorEventHandler(EventProducer eventProducer) {
        super(eventProducer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        eventProducer.sendSensorEvent(mapToSensorEventAvro(event));
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEventProto event) {
        LightSensorEventProto lightSensorEvent = event.getLightSensorEvent();
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }
}
