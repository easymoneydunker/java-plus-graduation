package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.model.Event;

import java.util.List;

public interface ViewService {
    void registerAll(List<Event> events, HttpServletRequest request);

    void register(Event event, HttpServletRequest request);
}
