package ru.yandex.practicum.mapper.proto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.HubEvent;

import java.time.Instant;

@Component
public class DeviceRemovedEventMapper implements HubEventMapper {

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public HubEvent map(HubEventProto event) {
        DeviceRemovedEventProto hubEvent = event.getDeviceRemoved();

        return DeviceRemovedEvent.builder()
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .id(hubEvent.getId())
                .build();
    }
}
