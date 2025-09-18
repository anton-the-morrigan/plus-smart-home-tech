package ru.yandex.practicum.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HubEventMapper {

    public HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        return new HubEventAvro(
                hubEvent.getHubId(),
                hubEvent.getTimestamp(),
                toHubEventPayloadAvro(hubEvent)
        );
    }

    public SpecificRecordBase toHubEventPayloadAvro(HubEvent hubEvent) {
        switch (hubEvent.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent event = (DeviceAddedEvent) hubEvent;
                return new DeviceAddedEventAvro(
                        event.getId(),
                        toDeviceTypeAvro(event.getDeviceType())
                );
            }

            case DEVICE_REMOVED -> {
                DeviceRemovedEvent event = (DeviceRemovedEvent) hubEvent;
                return new DeviceRemovedEventAvro(
                        event.getId()
                );
            }

            case SCENARIO_ADDED -> {
                ScenarioAddedEvent event = (ScenarioAddedEvent) hubEvent;
                return new ScenarioAddedEventAvro(
                        event.getName(),
                        toScenarioConditionAvro(event.getConditions()),
                        toDeviceActionAvro(event.getActions())
                );
            }

            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent event = (ScenarioRemovedEvent) hubEvent;
                return new ScenarioRemovedEventAvro(
                        event.getName()
                );
            }

            default -> throw new IllegalStateException("Invalid payload");
        }
    }

    private DeviceTypeAvro toDeviceTypeAvro(DeviceType deviceType) {
        return DeviceTypeAvro.valueOf(deviceType.name());
    }

    private ConditionTypeAvro toConditionTypeAvro(ConditionType conditionType) {
        return ConditionTypeAvro.valueOf(conditionType.name());
    }

    private ConditionOperationAvro toConditionOperationAvro(ConditionOperation conditionOperation) {
        return ConditionOperationAvro.valueOf(conditionOperation.name());
    }

    private ActionTypeAvro toActionTypeAvro(ActionType actionType) {
        return ActionTypeAvro.valueOf(actionType.name());
    }

    private List<ScenarioConditionAvro> toScenarioConditionAvro(List<ScenarioCondition> scenarioConditions) {
        return scenarioConditions.stream()
                .map(scenarioCondition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(scenarioCondition.getSensorId())
                        .setType(toConditionTypeAvro(scenarioCondition.getConditionType()))
                        .setOperation(toConditionOperationAvro(scenarioCondition.getConditionOperation()))
                        .setValue(scenarioCondition.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<DeviceActionAvro> toDeviceActionAvro(List<DeviceAction> deviceActions) {
        return deviceActions.stream()
                .map(deviceAction -> DeviceActionAvro.newBuilder()
                        .setSensorId(deviceAction.getSensorId())
                        .setType(toActionTypeAvro(deviceAction.getType()))
                        .setValue(deviceAction.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
