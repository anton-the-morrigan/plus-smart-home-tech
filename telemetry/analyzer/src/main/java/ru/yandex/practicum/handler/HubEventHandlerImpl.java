package ru.yandex.practicum.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.DuplicateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.Mapper;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HubEventHandlerImpl implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;
    private final Mapper mapper;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        Object payload = event.getPayload();
        String hubId = event.getHubId();
        switch (payload) {
            case ScenarioAddedEventAvro eventAvro -> addScenario(eventAvro, hubId);
            case ScenarioRemovedEventAvro eventAvro -> deleteScenario(eventAvro, hubId);
            case DeviceAddedEventAvro eventAvro -> addDevice(eventAvro, hubId);
            default -> deleteDevice((DeviceRemovedEventAvro) payload, hubId);
        }
    }

    private void addScenario(ScenarioAddedEventAvro eventAvro, String hubId) {
        String name = eventAvro.getName();
        if (scenarioRepository.existsByHubIdAndName(hubId, name)) {
            throw new DuplicateException("Сценарий уже существует");
        }
        checkSensorIds(eventAvro, hubId);
        Map<String, Condition> conditions = eventAvro.getConditions().stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, condition -> Condition.builder()
                        .type(mapper.toConditionType(condition.getType()))
                        .operation(mapper.toConditionOperation(condition.getOperation()))
                        .value(extractValue(condition))
                        .build()));
        Map<String, Action> actions = eventAvro.getActions().stream()
                .collect(Collectors.toMap(DeviceActionAvro::getSensorId, action -> Action.builder()
                        .type(mapper.toActionType(action.getType()))
                        .value(action.getValue())
                        .build()
                ));
        actionRepository.saveAll(actions.values());
        conditionRepository.saveAll(conditions.values());
        scenarioRepository.save(Scenario.builder()
                .hubId(hubId)
                .name(name)
                .conditions(conditions)
                .actions(actions).build());
    }

    private void deleteScenario(ScenarioRemovedEventAvro eventAvro, String hubId) {
        String name = eventAvro.getName();
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseThrow(() -> new NotFoundException("Сценарий не найден"));
        Set<Long> conditionIds = scenario.getConditions().values().stream().map(Condition::getId).collect(Collectors.toSet());
        conditionRepository.deleteAllById(conditionIds);
        Set<Long> actionIds = scenario.getActions().values().stream().map(Action::getId).collect(Collectors.toSet());
        actionRepository.deleteAllById(actionIds);
        scenarioRepository.deleteById(scenario.getId());
    }

    private void addDevice(DeviceAddedEventAvro eventAvro, String hubId) {
        String sensorId = eventAvro.getId();
        if (sensorRepository.existsById(sensorId)) {
            throw new DuplicateException("Устройство уже существует");
        }
        sensorRepository.save(Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build());
    }

    private void deleteDevice(DeviceRemovedEventAvro eventAvro, String hubId) {
        String sensorId = eventAvro.getId();
        if (!sensorRepository.existsByIdAndHubId(sensorId, hubId)) {
            throw new NotFoundException("Устройство не существует");
        }
        sensorRepository.deleteById(sensorId);
    }

    private void checkSensorIds(ScenarioAddedEventAvro eventAvro, String hubId) {
        Set<String> ids = eventAvro.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId).collect(Collectors.toSet());
        if (ids.size() < eventAvro.getConditions().size()) {
            throw new DuplicateException("Недопустимо указывать одновременно два условия для одного и того же датчика");
        }
        if (sensorRepository.findByIdInAndHubId(ids, hubId).size() != ids.size()) {
            throw new NotFoundException("Id датчиков не найдены в рамках данного хаба");
        }
        ids = eventAvro.getActions().stream().map(DeviceActionAvro::getSensorId).collect(Collectors.toSet());
        if (ids.size() < eventAvro.getActions().size()) {
            throw new DuplicateException("Недопустимо указывать одновременно два действия для одного и того же устройства");
        }
        if (sensorRepository.findByIdInAndHubId(ids, hubId).size() != ids.size()) {
            throw new DuplicateException("Id устройств не найдены в рамках данного хаба");
        }
    }

    private Integer extractValue(ScenarioConditionAvro conditionAvro) {
        Object valueObj = conditionAvro.getValue();
        if (valueObj instanceof Integer) {
            return (Integer) valueObj;
        }
        return (Boolean) valueObj ? 1 : 0;
    }
}