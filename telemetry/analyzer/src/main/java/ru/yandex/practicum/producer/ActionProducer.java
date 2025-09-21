package ru.yandex.practicum.producer;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.enums.ActionType;

import java.time.Instant;

@Slf4j
@Service
public class ActionProducer {
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void sendAction(Scenario scenario) {
        try {
            log.info("ActionProducer sendAction");
            String hubId = scenario.getHubId();
            String scenarioName = scenario.getName();
            for (Action action : scenario.getActions()) {
                DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensor().getId())
                        .setType(toActionType(action.getType()))
                        .setValue(action.getValue())
                        .build();

                Instant instant = Instant.now();

                Timestamp timestamp = Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build();

                DeviceActionRequestProto request = DeviceActionRequestProto.newBuilder()
                        .setHubId(hubId)
                        .setScenarioName(scenarioName)
                        .setAction(deviceActionProto)
                        .setTimestamp(timestamp)
                        .build();

                hubRouterClient.handleDeviceAction(request);
            }

        } catch (Exception e) {
            log.error("Ошибка при отправке действия: {}", e.getMessage(), e);
            throw e;
        }
    }

    private ActionTypeProto toActionType(ActionType actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }
}