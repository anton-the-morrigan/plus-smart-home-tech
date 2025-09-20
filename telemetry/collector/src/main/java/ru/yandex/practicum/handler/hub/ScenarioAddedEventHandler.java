package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventHandler extends HubEventHandlerImpl<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(EventProducer eventProducer) {
        super(eventProducer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();
        return ScenarioAddedEventAvro.newBuilder()
                .setActions(scenarioAddedEvent.getActionList().stream()
                        .map(ScenarioAddedEventHandler::toDeviceActionAvro)
                        .collect(Collectors.toList()))
                .setConditions(scenarioAddedEvent.getConditionList().stream()
                        .map(ScenarioAddedEventHandler::toScenarioConditionAvro)
                        .collect(Collectors.toList()))
                .setName(scenarioAddedEvent.getName())
                .build();
    }

    private static DeviceActionAvro toDeviceActionAvro(DeviceActionProto deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .build();
    }

    private static ScenarioConditionAvro toScenarioConditionAvro(ScenarioConditionProto scenarioCondition) {
        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()))
                .setSensorId(scenarioCondition.getSensorId());

        switch (scenarioCondition.getValueCase()) {
            case BOOL_VALUE:
                builder.setValue(scenarioCondition.getBoolValue());
                break;
            case INT_VALUE:
                builder.setValue(scenarioCondition.getIntValue());
                break;
            case VALUE_NOT_SET:
                builder.setValue(null);
                break;
        }

        return builder.build();
    }
}
