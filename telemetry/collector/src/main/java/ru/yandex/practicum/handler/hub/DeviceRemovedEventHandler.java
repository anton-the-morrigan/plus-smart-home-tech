package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedEventHandler extends HubEventHandlerImpl<DeviceRemovedEventAvro> {

    public DeviceRemovedEventHandler(EventProducer eventProducer) {
        super(eventProducer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEventProto event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getDeviceRemoved().getId())
                .build();
    }
}
