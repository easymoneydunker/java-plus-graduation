package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getAllByUserId(Long userId, int from, int size);

    EventFullDto addEvent(Long userId, @Valid NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, @Valid UpdateEventUserRequest request);

    List<RequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort, int from, int size,
                                        HttpServletRequest request);

    EventFullDto getPublicEvent(Long id, HttpServletRequest request);

    @Transactional
    EventFullDto getPublicEventForFeign(Long id, HttpServletRequest request);

    List<EventFullDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request);

    Event getOrThrow(Long eventId);

    @Transactional
    void updateConfirmedRequests(Long eventId, int countDelta);
}
