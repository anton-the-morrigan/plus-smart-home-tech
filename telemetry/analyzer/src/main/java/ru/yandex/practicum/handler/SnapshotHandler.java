package ru.yandex.practicum.handler;

import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

public interface SnapshotHandler {

    List<DeviceActionRequestProto> handle(SensorsSnapshotAvro snapshot);
}
