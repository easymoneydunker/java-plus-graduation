package ru.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.feign.config.FeignClientConfig;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "event-service", configuration = FeignClientConfig.class)
public interface EventClient {

    @GetMapping("/admin/events")
    List<EventFullDto> getAdminEvents(@RequestParam(required = false) List<Long> users,
                                      @RequestParam(required = false) List<String> states,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size);

    @GetMapping("/users/{userId}/events")
    List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size);

    @PostMapping("/users/{userId}/events")
    EventFullDto createEvent(@PathVariable Long userId,
                             @RequestBody NewEventDto newEventDto);

    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getUserEvent(@PathVariable Long userId,
                              @PathVariable Long eventId);

    @RequestMapping(method = RequestMethod.PUT, value = "/users/{userId}/events/{eventId}")
    EventFullDto updateUserEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody UpdateEventUserRequest request);

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    List<RequestDto> getEventRequests(@PathVariable Long userId,
                                      @PathVariable Long eventId);

    @GetMapping("/events")
    List<EventShortDto> getPublicEvents(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size);

    @GetMapping("/events/{eventId}")
    EventFullDto getPublicEventById(@PathVariable Long eventId);

    @PutMapping("/events/{eventId}/confirmations")
    void updateConfirmedRequests(@PathVariable Long eventId,
                                 @RequestParam int countDelta);
}
