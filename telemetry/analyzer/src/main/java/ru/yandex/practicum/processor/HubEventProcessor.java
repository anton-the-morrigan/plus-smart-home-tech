package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.HubEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.handler.HubEventHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final String TELEMETRY_HUBS_TOPIC = "telemetry.hubs.v1";

    private final HubEventHandler hubEventHandler;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

    KafkaConsumer<String, HubEventAvro> hubConsumer = new KafkaConsumer<>(getConsumerProperties());

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));
            hubConsumer.subscribe(List.of(TELEMETRY_HUBS_TOPIC));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        hubConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    hubEventHandler.handle(record.value());
                    currentOffset.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );
                }
                hubConsumer.commitAsync((offsets, exception) -> {
                });
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } finally {
            try {
                hubConsumer.commitSync(currentOffset);
            } finally {
                hubConsumer.close();
            }
        }
    }

    private Properties getConsumerProperties() {
        Properties config = new Properties();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-consumer");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        return config;
    }
}
