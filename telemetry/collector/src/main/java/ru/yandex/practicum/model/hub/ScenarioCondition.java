package ru.yandex.practicum.model.hub;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.hub.enums.ConditionOperation;
import ru.yandex.practicum.model.hub.enums.ConditionType;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {

    String sensorId;

    ConditionType type;

    ConditionOperation operation;

    Integer value;
}
