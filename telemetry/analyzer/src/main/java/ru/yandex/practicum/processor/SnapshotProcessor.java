package ru.yandex.practicum.processor;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.HubEventDeserializer;
import ru.yandex.practicum.SensorSnapshotDeserializer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.handler.SnapshotHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class SnapshotProcessor {
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final String TELEMETRY_SNAPSHOT_TOPIC = "telemetry.snapshots.v1";

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    private final SnapshotHandler snapshotHandler;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

    KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer = new KafkaConsumer<>(getConsumerProperties());

    public SnapshotProcessor(@GrpcClient("hub-router")
                             HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient,
                             Consumer<String, SensorsSnapshotAvro> snapshotConsumer,
                             SnapshotHandler snapshotHandler) {
        this.hubRouterClient = hubRouterClient;
        this.snapshotHandler = snapshotHandler;
    }

    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(snapshotConsumer::wakeup));
            snapshotConsumer.subscribe(List.of(TELEMETRY_SNAPSHOT_TOPIC));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        snapshotConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    for (DeviceActionRequest action : snapshotHandler.handle(record.value())) {
                        hubRouterClient.handleDeviceAction(action);
                    }
                    currentOffset.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );
                }
                snapshotConsumer.commitAsync((offsets, exception) -> {
                });
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } finally {
            try {
                snapshotConsumer.commitSync(currentOffset);
            } finally {
                snapshotConsumer.close();
            }
        }
    }

    private Properties getConsumerProperties() {
        Properties config = new Properties();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-consumer");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorSnapshotDeserializer.class);
        return config;
    }
}
