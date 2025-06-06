package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class PublicEventController {
    private final EventService eventService;

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
        return eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {

        return eventService.getPublicEvent(id, request);
    }
}
