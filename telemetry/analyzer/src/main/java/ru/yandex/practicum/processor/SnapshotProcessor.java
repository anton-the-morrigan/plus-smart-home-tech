package ru.yandex.practicum.processor;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.handler.SnapshotHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SnapshotProcessor {
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final String TELEMETRY_SNAPSHOT_TOPIC = "telemetry.snapshots.v1";

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final SnapshotHandler snapshotHandler;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();


    public SnapshotProcessor(@GrpcClient("hub-router")
                             HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient,
                             Consumer<String, SensorsSnapshotAvro> snapshotConsumer,
                             SnapshotHandler snapshotHandler) {
        this.hubRouterClient = hubRouterClient;
        this.snapshotConsumer = snapshotConsumer;
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
}
