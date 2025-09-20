package ru.yandex.practicum.handler;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaTopicsProperties;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class EventProducer {
    private final Producer<String, SpecificRecordBase> producer;
    private final KafkaTopicsProperties kafkaTopics;

    private void send(String topic, SpecificRecordBase event, long timestamp, String key) {
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                topic, null, timestamp, key, event);
        producer.send(producerRecord);
    }

    public void sendSensorEvent(SpecificRecordBase event) {
        SensorEventAvro avroEvent = (SensorEventAvro) event;
        String hubId = avroEvent.getHubId();
        long timestamp = avroEvent.getTimestamp().toEpochMilli();
        send(kafkaTopics.getTelemetrySensorsV1(), event, timestamp, hubId);
    }

    public void sendHubEvent(SpecificRecordBase event) {
        HubEventAvro avroEvent = (HubEventAvro) event;
        String hubId = avroEvent.getHubId();
        long timestamp = avroEvent.getTimestamp().toEpochMilli();
        send(kafkaTopics.getTelemetryHubsV1(), event, timestamp, hubId);
    }

    @PreDestroy
    public void stop() {
        producer.flush();
        producer.close(Duration.ofSeconds(30));
    }

}
