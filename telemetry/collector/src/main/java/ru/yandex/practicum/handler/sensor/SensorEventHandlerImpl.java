package ru.yandex.practicum.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class SensorEventHandlerImpl<T extends SpecificRecordBase> implements SensorEventHandler {

    protected final EventProducer eventProducer;

    protected SensorEventAvro mapToSensorEventAvro(SensorEventProto event) {
        Instant timestamp = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        );
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(timestamp)
                .setPayload(mapToAvro(event))
                .build();
    }
    protected abstract T mapToAvro(SensorEventProto event);
}
