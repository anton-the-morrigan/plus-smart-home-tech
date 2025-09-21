package ru.yandex.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.enums.ActionType;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnapshotHandlerImpl implements SnapshotHandler {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    private final ScenarioRepository scenarioRepository;

    @Override
    public List<DeviceActionRequestProto> handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        if (snapshots.containsKey(hubId)) {
            SensorsSnapshotAvro sensorsSnapshotAvro = snapshots.get(hubId);
            Instant oldSnapshotTimestamp = sensorsSnapshotAvro.getTimestamp();
            Instant newSnapshotTimestamp = snapshot.getTimestamp();
            if (oldSnapshotTimestamp.isAfter(newSnapshotTimestamp)) {
                return List.of();
            }
        }
        snapshots.put(hubId, snapshot);
        List<DeviceActionRequestProto> actionRequests = new ArrayList<>();
        for (Scenario scenario : scenarioRepository.findByHubId(snapshot.getHubId())) {
            Map<String, Condition> conditions = scenario.getConditions();
            if (!snapshot.getSensorsState().keySet().containsAll(conditions.keySet())) {
                log.debug("Неполный снапшот от хаба {}: отсутствуют показания некоторых датчиков", snapshot.getHubId());
                continue;
            }
            if (checkConditions(conditions, snapshot)) {
                Map<String, Action> actions = scenario.getActions();
                for (Map.Entry<String, Action> entry : actions.entrySet()) {
                    Action action = entry.getValue();
                    actionRequests.add(DeviceActionRequestProto.newBuilder()
                            .setScenarioName(scenario.getName())
                            .setHubId(scenario.getHubId())
                            .setAction(DeviceActionProto.newBuilder()
                                    .setSensorId(entry.getKey())
                                    .setType(mapToActionTypeProto(action.getType()))
                                    .setValue(action.getValue())
                                    .build())
                            .build());
                }
            }
        }
        return actionRequests;
    }

    private boolean checkConditions(Map<String, Condition> conditions, SensorsSnapshotAvro snapshotAvro) {
        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            Object data = snapshotAvro.getSensorsState().get(entry.getKey()).getData();
            Condition condition = entry.getValue();
            Integer value = condition.getValue();
            ConditionOperation conditionOperation = condition.getOperation();
            if (!switch (condition.getType()) {
                case TEMPERATURE -> {
                    if (data instanceof TemperatureSensorAvro temperatureState) {
                        yield checkByOperationType(temperatureState.getTemperatureC(), value, conditionOperation);
                    } else {
                        ClimateSensorAvro climateState = (ClimateSensorAvro) data;
                        yield checkByOperationType(climateState.getTemperatureC(), value, conditionOperation);
                    }
                }
                case LUMINOSITY -> {
                    LightSensorAvro lightSensorState = (LightSensorAvro) data;
                    yield checkByOperationType(lightSensorState.getLuminosity(), value, conditionOperation);
                }
                case HUMIDITY -> {
                    ClimateSensorAvro climateSensorState = (ClimateSensorAvro) data;
                    yield checkByOperationType(climateSensorState.getHumidity(), value, conditionOperation);
                }
                case CO2LEVEL -> {
                    ClimateSensorAvro climateSensorState = (ClimateSensorAvro) data;
                    yield checkByOperationType(climateSensorState.getCo2Level(), value, conditionOperation);
                }
                case SWITCH -> {
                    SwitchSensorAvro switchSensorState = (SwitchSensorAvro) data;
                    yield (switchSensorState.getState() ? 1 : 0) == value;
                }
                case MOTION -> {
                    MotionSensorAvro motionSensorState = (MotionSensorAvro) data;
                    yield (motionSensorState.getMotion() ? 1 : 0) == value;
                }
            }) return false;
        }
        return true;
    }

    private boolean checkByOperationType(int currentValue, int conditionValue, ConditionOperation type) {
        return switch (type) {
            case EQUALS -> currentValue == conditionValue;
            case GREATER_THAN -> currentValue > conditionValue;
            case LOWER_THAN -> currentValue < conditionValue;
        };
    }

    private ActionTypeProto mapToActionTypeProto(ActionType actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }
}
