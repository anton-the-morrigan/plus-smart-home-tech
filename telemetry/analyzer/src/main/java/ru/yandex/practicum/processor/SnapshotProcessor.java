package ru.yandex.practicum.processor;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.snapshot.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Component
public class SnapshotProcessor implements Runnable {
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final String TELEMETRY_SNAPSHOT_TOPIC = "telemetry.snapshots.v1";

    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final SnapshotHandler snapshotHandler;

    public SnapshotProcessor(Consumer<String, SensorsSnapshotAvro> snapshotConsumer, SnapshotHandler snapshotHandler) {
        this.snapshotConsumer = snapshotConsumer;
        this.snapshotHandler = snapshotHandler;
    }

    public void run() {
        try {
            snapshotConsumer.subscribe(List.of(TELEMETRY_SNAPSHOT_TOPIC));
            Runtime.getRuntime().addShutdownHook(new Thread(snapshotConsumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshotAvro = record.value();
                    snapshotHandler.handleSnapshot(snapshotAvro);
                }
                snapshotConsumer.commitSync();

            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } finally {
            try {
                snapshotConsumer.commitSync();
            } finally {
                snapshotConsumer.close();
            }
        }
    }
}
