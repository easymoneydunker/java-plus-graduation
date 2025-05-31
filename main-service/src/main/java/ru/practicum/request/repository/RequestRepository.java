package ru.practicum.request.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);
}
