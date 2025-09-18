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
                return ScenarioAddedEventAvro.newBuilder()
                        .setName(event.getName())
                        .setConditions(event.getConditions().stream()
                                .map(this::toConditionAvro)
                                .toList())
                        .setActions(event.getActions().stream()
                                .map(this::toActionAvro)
                                .toList())
                        .build();
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

    private static DeviceTypeAvro toDeviceTypeAvro(DeviceType deviceType) {
        return DeviceTypeAvro.valueOf(deviceType.name());
    }

    private ScenarioConditionAvro toConditionAvro(ScenarioCondition scenarioCondition) {
        ConditionTypeAvro conditionTypeAvro = switch (scenarioCondition.getConditionType()) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
        };
        ConditionOperationAvro conditionOperationAvro = switch (scenarioCondition.getConditionOperation()) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
        };
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(conditionTypeAvro)
                .setOperation(conditionOperationAvro)
                .build();
    }

    private DeviceActionAvro toActionAvro(DeviceAction deviceAction) {
        ActionTypeAvro actionTypeAvro = switch (deviceAction.getType()) {
            case INVERSE -> ActionTypeAvro.INVERSE;
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
        };
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(actionTypeAvro)
                .setValue(deviceAction.getValue())
                .build();
    }
}
