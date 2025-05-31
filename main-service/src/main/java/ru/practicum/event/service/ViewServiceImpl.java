package ru.practicum.event.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventView;
import ru.practicum.event.repository.ViewRepository;

@Service
@AllArgsConstructor
public class ViewServiceImpl implements ViewService {
    private final ViewRepository viewRepository;

    private static EventView getEventView(HttpServletRequest request, Event event) {
        return EventView.builder()
                .ip(request.getRemoteAddr())
                .event(event)
                .build();
    }

    @Override
    public void registerAll(List<Event> events, HttpServletRequest request) {
        List<EventView> views = events.stream().map(event -> getEventView(request, event))
                .filter(view -> viewRepository.findByIpAndEventId(view.getIp(), view.getEvent().getId()).isEmpty())
                .toList();

        viewRepository.saveAllAndFlush(views);
    }

    @Override
    public void register(Event event, HttpServletRequest request) {
        if (viewRepository.findByIpAndEventId(request.getRemoteAddr(), event.getId()).isPresent()) {
            return;
        }
        EventView eventView = getEventView(request, event);
        viewRepository.saveAndFlush(eventView);
    }
}
