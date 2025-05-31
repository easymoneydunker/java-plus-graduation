package ru.practicum.event.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.service.CategoriesService;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.common.PageableBuilder;
import ru.practicum.common.ValidationException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.ReqMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.service.RequestService;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final RequestService requestService;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventMapper mapper;
    private final ReqMapper requestMapper;
    private final UserService userService;
    private final CategoriesService categoryService;
    private final ViewService viewService;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUserId(Long userId, int from, int size) {
        var pageable = PageableBuilder.getPageable(from, size, "id");
        return eventRepository.findAllByUserId(userId, pageable).getContent().stream().map(mapper::toShortDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event event = mapper.toEntity(newEventDto);

        final Location requestLocation = event.getLocation();
        Location mayBeExistingLocation = null;
        if (requestLocation.getId() == null) {
            mayBeExistingLocation = locationRepository
                    .findByLatAndLon(requestLocation.getLat(), requestLocation.getLon())
                    .orElseGet(() -> locationRepository.save(requestLocation));
        }

        event.setLocation(mayBeExistingLocation);
        event.setUser(userService.getOrThrow(userId));
        event = eventRepository.save(event);

        return mapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndUser_Id(eventId, userId).orElseThrow(() -> new NotFoundException(
                "Event with id " + eventId + " and user id " + userId + " was not found"));
        return mapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new IllegalArgumentException("Not valid time. Should not be less than now + 2 hours.");
        }
        Event event = eventRepository.findByIdAndUser_Id(eventId, userId).orElseThrow(() -> new NotFoundException(
                "Event with id " + eventId + " was not found"));

        var updateBuilder = event.toBuilder();

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (request.getStateAction() != null) {
            Map<StateAction, EventState> map = Map.of(
                    StateAction.SEND_TO_REVIEW, EventState.PENDING,
                    StateAction.CANCEL_REVIEW, EventState.CANCELED
            );

            updateBuilder.state(map.get(request.getStateAction()));
        }

        if (request.getLocation() != null) {
            final Location newLocation = request.getLocation();
            Location updatedLocation = null;
            if (newLocation.getId() == null) {
                updatedLocation =
                        locationRepository
                                .findByLatAndLon(newLocation.getLat(), newLocation.getLon())
                                .orElseGet(() -> locationRepository.save(newLocation));
            }

            updateBuilder.location(updatedLocation);
        }

        Optional.ofNullable(request.getAnnotation()).ifPresent(updateBuilder::annotation);
        Optional.ofNullable(request.getCategory()).ifPresent(catId ->
                updateBuilder.category(categoryService.getOrThrow(catId)));
        Optional.ofNullable(request.getDescription()).ifPresent(updateBuilder::description);
        Optional.ofNullable(request.getEventDate()).ifPresent(updateBuilder::eventDate);
        Optional.ofNullable(request.getPaid()).ifPresent(updateBuilder::paid);
        Optional.ofNullable(request.getParticipantLimit()).ifPresent(updateBuilder::participantLimit);
        Optional.ofNullable(request.getRequestModeration()).ifPresent(updateBuilder::requestModeration);
        Optional.ofNullable(request.getTitle()).ifPresent(updateBuilder::title);

        event = updateBuilder.build();

        return mapper.toFullDto(eventRepository.saveAndFlush(event));
    }

    @Override
    @Transactional
    public List<RequestDto> getRequests(Long userId, Long eventId) {
        return requestService.getRequests(userId, eventId);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest updateRequest) {
        List<RequestDto> requests = requestService.getRequestsByUserIdAndEventIdAndRequestIdIn(userId, eventId,
                updateRequest.getRequestIds());
        int confirmedRequestCount = requestService.getConfirmedRequests(eventId, RequestStatus.CONFIRMED).size();

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        Event event = getOrThrow(eventId);
        int size = updateRequest.getRequestIds().size();
        int confirmedSize = updateRequest.getStatus() == RequestStatus.CONFIRMED ? size : 0;
        if (event.getParticipantLimit() - confirmedRequestCount < confirmedSize) {
            throw new ConflictException("Event limit exceed. Only " +
                    (event.getParticipantLimit() - confirmedRequestCount) + " places left.");
        }

        for (RequestDto request : requests) {
            if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.toParticipationRequestDto(request));
            } else if (updateRequest.getStatus() == RequestStatus.REJECTED) {
                if (request.getStatus() == RequestStatus.CONFIRMED) {
                    throw new ConflictException("Forbidden to reject confirmed request.");
                }
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toParticipationRequestDto(request));
            }
        }

        requestService.saveAll(requests);

        return EventRequestStatusUpdateResult.builder().confirmedRequests(confirmedRequests).rejectedRequests(rejectedRequests).build();
    }

    @Override
    @Transactional
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size,
                                               HttpServletRequest request) {
        assertDataValid(rangeStart, rangeEnd);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> event = cq.from(Event.class);

        Predicate predicate = cb.conjunction();

        // Фильтр по состоянию
        predicate = cb.and(predicate, cb.equal(event.get("state"), "PUBLISHED"));

        // Фильтр для текста поиска
        if (text != null && !text.isEmpty()) {
            String likePattern = "%" + text.toLowerCase() + "%";
            Predicate textPredicate = cb.or(
                    cb.like(cb.lower(event.get("annotation")), likePattern),
                    cb.like(cb.lower(event.get("description")), likePattern)
            );
            predicate = cb.and(predicate, textPredicate);
        }

        // Фильтр по категориям
        if (categories != null && !categories.isEmpty()) {
            predicate = cb.and(predicate, event.get("category").get("id").in(categories));
        }

        // Фильтр по оплаченным статусам
        if (paid != null) {
            predicate = cb.and(predicate, cb.equal(event.get("paid"), paid));
        }

        // Фильтр по дате
        if (rangeStart != null && rangeEnd != null) {
            predicate = cb.and(predicate, cb.between(event.get("eventDate"), rangeStart, rangeEnd));
        } else if (rangeStart != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(event.get("eventDate"), rangeStart));
        } else if (rangeEnd != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(event.get("eventDate"), rangeEnd));
        }

        // Фильтр по доступности
        if (onlyAvailable != null && onlyAvailable) {
            Predicate availabilityPredicate = cb.or(
                    cb.equal(event.get("participantLimit"), 0),
                    cb.lessThan(event.get("confirmedRequests"), event.get("participantLimit"))
            );
            predicate = cb.and(predicate, availabilityPredicate);
        }

        // Применение предиката
        cq.where(predicate);

        // Определение сортировки
        if ("EVENT_DATE".equals(sort)) {
            cq.orderBy(cb.asc(event.get("eventDate")));
        } else if ("VIEWS".equals(sort)) {
            cq.orderBy(cb.desc(event.get("views")));
        }

        // Создание запроса
        TypedQuery<Event> query = entityManager.createQuery(cq);

        // Установка параметров для пагинации
        query.setFirstResult(from * size);
        query.setMaxResults(size);

        // Выполнение запроса
        List<Event> resultList = query.getResultList();
        eventRepository.saveAll(resultList);
        viewService.registerAll(resultList, request);
        return resultList.stream().map(mapper::toShortDto).toList();
    }

    private void assertDataValid(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart == null || rangeEnd == null) {
            return;
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("start after end.");
        }
    }

    @Override
    @Transactional
    public EventFullDto getPublicEvent(Long id, HttpServletRequest request) {
        Event event =
                eventRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(() -> new NotFoundException(
                        "Event with id " + id + " not found or not published"));
        eventRepository.saveAndFlush(event);
        viewService.register(event, request);
        return mapper.toFullDto(event);
    }

    @Override
    @Transactional
    public List<EventFullDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        var page = from / size;
        assertDataValid(rangeStart, rangeEnd);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> event = cq.from(Event.class);

        Predicate predicate = cb.conjunction();

        if (users != null && !users.isEmpty()) {
            predicate = cb.and(predicate, event.get("user").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            predicate = cb.and(predicate, event.get("state").in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = cb.and(predicate, event.get("category").get("id").in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            predicate = cb.and(predicate, cb.between(event.get("eventDate"), rangeStart, rangeEnd));
        }

        cq.where(predicate);
        TypedQuery<Event> query = entityManager.createQuery(cq);

        // setting pagination parameters
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList().stream().map(mapper::toFullDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " was not found"));

        validateEventStateForAdminUpdate(event, request.getStateAction());

        Location location = request.getLocation();
        if (location != null && location.getId() == null) {
            locationRepository.save(location);
        }

        mapper.updateFromAdminRequest(request, event);
        event.setState(request.getStateAction() == StateAction.PUBLISH_EVENT ? EventState.PUBLISHED :
                EventState.CANCELED);
        return mapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public Event getOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " was not found"));
    }

    private void validateEventStateForAdminUpdate(Event event, StateAction stateAction) {
        if (stateAction == StateAction.PUBLISH_EVENT && event.getState() != EventState.PENDING) {
            throw new ConflictException("Cannot publish the event because it's not in the right state");
        }
        if (stateAction == StateAction.REJECT_EVENT && event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot cancel the event because it has been published");
        }
    }
}
