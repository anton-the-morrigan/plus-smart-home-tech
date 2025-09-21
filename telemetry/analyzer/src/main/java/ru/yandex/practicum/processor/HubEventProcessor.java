package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final String TELEMETRY_HUBS_TOPIC = "telemetry.hubs.v1";

    private final Consumer<String, HubEventAvro> hubConsumer;
    private final Map<String, HubEventHandler> hubHandlers;

    public HubEventProcessor(Consumer<String, HubEventAvro> hubConsumer, Set<HubEventHandler> hubHandlers) {
        this.hubConsumer = hubConsumer;
        this.hubHandlers = hubHandlers.stream().collect(Collectors.toMap(HubEventHandler::getType, Function.identity()));
    }

    @Override
    public void run() {
        log.debug("HubEventProcessor run");
        try {
            hubConsumer.subscribe(List.of(TELEMETRY_HUBS_TOPIC));
            Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = hubConsumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro hubEvent = record.value();
                    String payload = hubEvent.getPayload().getClass().getName();

                    if (hubHandlers.containsKey(payload)) {
                        hubHandlers.get(payload).handle(hubEvent);
                    } else {
                        throw new IllegalArgumentException("отсутствует хендлер для события " + hubEvent);
                    }
                }
                hubConsumer.commitSync();
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } finally {
            try {
                hubConsumer.commitSync();
            } finally {
                hubConsumer.close();
            }
        }
    }
}
