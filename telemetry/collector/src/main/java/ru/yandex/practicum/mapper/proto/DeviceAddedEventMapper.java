package ru.yandex.practicum.mapper.proto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.DeviceType;
import ru.yandex.practicum.model.hub.HubEvent;

import java.time.Instant;

@Component
public class DeviceAddedEventMapper implements HubEventMapper {

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public HubEvent map(HubEventProto event) {
        DeviceAddedEventProto hubEvent = event.getDeviceAdded();

        return DeviceAddedEvent.builder()
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .id(hubEvent.getId())
                .deviceType(DeviceType.valueOf(hubEvent.getType().name()))
                .build();
    }
}
