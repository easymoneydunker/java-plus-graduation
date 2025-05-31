package ru.practicum.event.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.model.Event;

public interface ViewService {
    void registerAll(List<Event> events, HttpServletRequest request);

    void register(Event event, HttpServletRequest request);
}
