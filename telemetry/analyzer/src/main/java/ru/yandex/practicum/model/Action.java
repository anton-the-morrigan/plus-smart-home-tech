package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.enums.ActionType;

import java.util.List;

@Entity
@Table(name = "actions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    Long id;
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    Sensor sensor;

    @Enumerated(EnumType.STRING)
    ActionType type;

    Integer value;

    @ManyToMany(mappedBy = "actions")
    List<Scenario> scenarios;
}