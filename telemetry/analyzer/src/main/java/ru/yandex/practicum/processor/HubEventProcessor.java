package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.KafkaConsumerProperties;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.handler.HubEventHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final String TELEMETRY_HUBS_TOPIC = "telemetry.hubs.v1";

    private final Consumer<String, HubEventAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();
    private final HubEventHandler hubEventHandler;
    private final KafkaConsumerProperties properties;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(TELEMETRY_HUBS_TOPIC));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        consumer.poll(Duration.ofSeconds(properties.getPollDurationSeconds().getHubEvent()));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        hubEventHandler.handle(record.value());
                    } catch (DuplicateException | NotFoundException e) {
                        log.info("Произошло исключение");
                    }
                    currentOffset.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );
                }
                consumer.commitAsync((offsets, exception) -> {});
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хабов", e);
        } finally {
            try {
                consumer.commitSync(currentOffset);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}