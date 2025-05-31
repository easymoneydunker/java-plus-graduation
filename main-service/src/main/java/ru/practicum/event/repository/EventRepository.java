package ru.practicum.event.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where e.user.id = :userId")
    Page<Event> findAllByUserId(Long userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> events);

    Optional<Event> findByIdAndState(Long id, EventState state);

    Optional<Event> findByIdAndUser_Id(Long eventId, Long userId);

    Optional<List<Event>> findAllByCategoryId(Long id);
}
