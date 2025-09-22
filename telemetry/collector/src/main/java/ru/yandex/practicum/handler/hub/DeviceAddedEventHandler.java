package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedEventHandler extends HubEventHandlerImpl<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(EventProducer eventProducer) {
        super(eventProducer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventProto event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getDeviceAdded().getId())
                .setType(DeviceTypeAvro.valueOf(event.getDeviceAdded().getType().name()))
                .build();
    }
}
