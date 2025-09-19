package ru.yandex.practicum.mapper.proto;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.model.hub.HubEvent;

public interface HubEventMapper {
    HubEventProto.PayloadCase getMessageType();

    HubEvent map(HubEventProto event);
}
