package ru.practicum.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.EventView;

@Repository
public interface ViewRepository extends JpaRepository<EventView, Long> {
    Optional<EventView> findByIpAndEventId(String ip, Long eventId);
}
