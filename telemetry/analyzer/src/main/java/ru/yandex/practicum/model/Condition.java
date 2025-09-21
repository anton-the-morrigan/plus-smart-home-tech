package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.enums.ConditionType;
import ru.yandex.practicum.model.enums.OperationType;

import java.util.List;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    Sensor sensor;

    @Enumerated(EnumType.STRING)
    ConditionType type;

    @Enumerated(EnumType.STRING)
    OperationType operation;

    Integer value;

    @ManyToMany(mappedBy = "conditions")
    List<Scenario> scenarios;
}
