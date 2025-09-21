package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Sensor;

import java.util.Collection;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, String> {
    boolean existsByIdAndHubId(String id, String hubId);

    Collection<Sensor> findByIdInAndHubId(Collection<String> ids, String hubId);
}
