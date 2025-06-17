package ru.practicum.event.controller;

import com.google.protobuf.Timestamp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.CollectorClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRecommendationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.grpc.stats.actions.ActionTypeProto;
import ru.practicum.grpc.stats.actions.UserActionProto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Slf4j
public class PublicEventController {
    private final static String X_EWM_USER_ID = "X-EWM-USER-ID";
    private final EventService eventService;
    private final CollectorClient collectorClient;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) {
        log.info("GET /events | text={}, categories={}, paid={}," +
                        " rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService
                .getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, @RequestHeader("X-EWM-USER-ID") long userId) {
        log.info("GET /events/{}", id);
        EventFullDto event = eventService.getPublicEvent(id);
        collectorClient.sendUserAction(createUserAction(id, userId, ActionTypeProto.ACTION_VIEW, Instant.now()));
        return event;
    }

    @GetMapping("/{id}/feign")
    public EventFullDto getEventFeign(@PathVariable Long id) {
        log.info("GET /events/{}", id);
        return eventService.getPublicEventForFeign(id);
    }

    @PatchMapping("/{eventId}/confirmations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateConfirmedRequests(
            @PathVariable Long eventId,
            @RequestParam int countDelta) {
        eventService.updateConfirmedRequests(eventId, countDelta);
    }

    @PutMapping("/{eventId}/confirmations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateConfirmedRequestsWithPut(
            @PathVariable Long eventId,
            @RequestParam int countDelta) {
        eventService.updateConfirmedRequests(eventId, countDelta);
    }

    @GetMapping("/recommendations")
    public List<EventRecommendationDto> getRecommendations(@RequestHeader(X_EWM_USER_ID) long userId) {
        return eventService.getRecommendations(userId);
    }

    @PutMapping("/{eventId}/like")
    public void addLike(@PathVariable Long eventId, @RequestHeader(X_EWM_USER_ID) long userId) {
        eventService.addLike(eventId, userId);
    }


    private UserActionProto createUserAction(Long eventId, Long userId, ActionTypeProto type, Instant timestamp) {
        return UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(type)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
    }
}
