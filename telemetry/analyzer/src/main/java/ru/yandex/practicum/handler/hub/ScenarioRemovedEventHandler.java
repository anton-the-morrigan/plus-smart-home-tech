package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;

    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {
        String name = ((ScenarioRemovedEventAvro) hubEventAvro.getPayload()).getName();
        scenarioRepository.deleteByHubIdAndName(hubEventAvro.getHubId(),name);
    }
}
