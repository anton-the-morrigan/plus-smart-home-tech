package ru.yandex.practicum.handler.snapshot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.producer.ActionProducer;
import ru.yandex.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.enums.OperationType;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SnapshotHandler {
    private final ScenarioRepository scenarioRepository;
    private final ActionProducer ActionProducer;
    private final Map<String, SensorEventHandler> sensorEventHandlers;

    public SnapshotHandler(ScenarioRepository scenarioRepository,
                           Set<SensorEventHandler> sensorEventHandlers, ActionProducer scenarioActionProducer) {
        this.scenarioRepository = scenarioRepository;
        this.ActionProducer = scenarioActionProducer;
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getType,
                        Function.identity()
                ));
    }

    public void handleSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        log.debug("SnapshotHandler handleSnapshot");
        List<Scenario> scenarios = getScenariosBySnapshots(sensorsSnapshotAvro);
        for (Scenario scenario : scenarios) {
            ActionProducer.sendAction(scenario);
        }
    }

    private List<Scenario> getScenariosBySnapshots(SensorsSnapshotAvro sensorsSnapshotAvro) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshotAvro.getHubId());
        Map<String, SensorStateAvro> sensorStates = sensorsSnapshotAvro.getSensorsState();

        return scenarios.stream()
                .filter(scenario -> checkConditions(scenario.getConditions(), sensorStates))
                .toList();
    }

    private boolean checkConditions(List<Condition> conditions, Map<String, SensorStateAvro> sensorStates) {

        return conditions.stream().allMatch(condition -> {
            try {
                return checkCondition(condition, sensorStates.get(condition.getSensor().getId()));
            } catch (Exception e) {
                return false;
            }
        });
    }

    private boolean checkCondition(Condition condition, SensorStateAvro sensorStateAvro) {
        String type = sensorStateAvro.getData().getClass().getTypeName();
        if (!sensorEventHandlers.containsKey(type)) {
            throw new IllegalArgumentException("Нет обработчика для сенсора " + type);
        }

        Integer value = sensorEventHandlers.get(type).getSensorValue(condition.getType(), sensorStateAvro);

        if(value == null) {
            return false;
        }

        return switch (condition.getOperation()) {
            case OperationType.LOWER_THAN -> value < condition.getValue();
            case OperationType.EQUALS -> value.equals(condition.getValue());
            case OperationType.GREATER_THAN -> value > condition.getValue();
        };
    }

}
