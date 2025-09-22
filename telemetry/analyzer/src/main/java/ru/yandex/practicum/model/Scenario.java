package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Entity
@Table(name = "scenarios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hub_id", "name"}))
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "hub_id")
    String hubId;

    String name;

    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinTable(
            name = "scenario_conditions",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "condition_id"))
    @MapKeyColumn(
            table = "scenario_conditions",
            name = "sensor_id"
    )
    Map<String, Condition> conditions;

    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinTable(
            name = "scenario_actions",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id"))
    @MapKeyColumn(
            table = "scenario_actions",
            name = "sensor_id"
    )

    Map<String, Action> actions;

}
