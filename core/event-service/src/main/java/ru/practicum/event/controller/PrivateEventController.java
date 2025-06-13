package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
@Slf4j
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest httpServletRequest) {
        log.info("GET /users/{}/events | from={}, size={}", userId, from, size);
        return eventService.getAllByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto,
                                 HttpServletRequest httpServletRequest) {
        log.info("POST /users/{}/events | newEventDto={}", userId, newEventDto);
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 HttpServletRequest httpServletRequest) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest request,
                                    HttpServletRequest httpServletRequest) {
        log.info("PATCH /users/{}/events/{} | request={}", userId, eventId, request);
        return eventService.updateEvent(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        HttpServletRequest httpServletRequest) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @RequestBody @Valid EventRequestStatusUpdateRequest request,
                                                         HttpServletRequest httpServletRequest) {
        log.info("PATCH /users/{}/events/{}/requests | request={}", userId, eventId, request);
        return eventService.updateRequest(userId, eventId, request);
    }
}
