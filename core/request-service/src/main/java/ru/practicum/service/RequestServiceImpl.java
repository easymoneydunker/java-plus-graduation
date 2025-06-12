package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final RequestMapper requestMapper;
    private final EventMapper eventMapper;

    @Override
    public List<RequestDto> getRequests(long userId) {
        log.info("Fetching requests for userId={}", userId);
        UserDto user = findUserById(userId);
        List<RequestDto> result = requestMapper.toDtos(requestRepository.findByRequesterId(user.getId()));
        log.debug("Found {} requests for userId={}", result.size(), userId);
        return result;
    }

    @Override
    public RequestDto createRequest(long userId, long eventId) {
        log.info("Creating request for userId={} and eventId={}", userId, eventId);

        if (eventId == 0) {
            log.error("Event id is zero");
            throw new ConflictException("Event id is zero");
        }

        EventFullDto event = findEventById(eventId);
        UserDto user = findUserById(userId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.warn("Request already exists for userId={} and eventId={}", userId, eventId);
            throw new ConflictException("Request already exist");
        }

        if (event.getInitiator().getId().equals(user.getId())) {
            log.warn("User {} is the event initiator, cannot create request", userId);
            throw new ConflictException("Request can't be created by initiator");
        }

        if (!EventState.PUBLISHED.equals(event.getState())) {
            log.warn("Event {} is not published, cannot create request", eventId);
            throw new ConflictException("Event not yet published");
        }

        int requestsSize = requestRepository.findAllByEventId(eventId).size();
        log.debug("Event {} has {} existing requests", eventId, requestsSize);

        if (event.getParticipantLimit() > 0 && !event.isRequestModeration() && event.getParticipantLimit() <= requestsSize) {
            log.warn("Participant limit exceeded for eventId={}", eventId);
            throw new ConflictException("Participant limit exceeded");
        }

        Request eventRequest = new Request(null, LocalDateTime.now(), eventId, userId, RequestStatus.PENDING);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            eventRequest.setStatus(RequestStatus.CONFIRMED);
        }

        if (eventRequest.getStatus() == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventClient.updateUserEvent(userId, eventId, eventMapper.toUpdateRequest(event));
            log.debug("Request confirmed immediately and event updated for eventId={}", eventId);
        }

        RequestDto saved = requestMapper.toDto(requestRepository.save(eventRequest));
        log.info("Request created successfully: {}", saved);
        return saved;
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        log.info("Cancelling requestId={} for userId={}", requestId, userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> {
                    log.warn("Request with id={} not found for userId={}", requestId, userId);
                    return new NotFoundException(MessageFormat.format("Request with id={0} was not found", requestId));
                });

        request.setStatus(RequestStatus.CANCELED);
        RequestDto result = requestMapper.toDto(requestRepository.save(request));
        log.info("Request canceled successfully: {}", result);
        return result;
    }

    @Override
    public List<RequestDto> getConfirmedRequests(Long eventId, RequestStatus status) {
        log.info("Fetching confirmed requests for eventId={} with status={}", eventId, status);
        List<RequestDto> result = requestRepository.findAllByEventIdAndStatus(eventId, status)
                .stream().map(requestMapper::toDto).collect(Collectors.toList());
        log.debug("Found {} confirmed requests", result.size());
        return result;
    }

    private UserDto findUserById(long userId) {
        log.debug("Fetching user by id={}", userId);
        return userClient.getUser(userId);
    }

    private EventFullDto findEventById(long eventId) {
        log.debug("Fetching event by id={}", eventId);
        return eventClient.getPublicEventById(eventId);
    }

    @Override
    public List<RequestDto> getRequests(long userId, long eventId) {
        log.info("Fetching requests for eventId={} initiated by userId={}", eventId, userId);
        EventFullDto event = findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("User {} is not the owner of event {}", userId, eventId);
            throw new NotFoundException("User is not the owner of the event");
        }

        List<RequestDto> result = requestRepository.findAllByEventId(eventId)
                .stream().map(requestMapper::toDto).collect(Collectors.toList());
        log.debug("Found {} requests for eventId={}", result.size(), eventId);
        return result;
    }

    @Override
    public List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIdIn(long userId, long eventId,
                                                                        List<Long> requestIds) {
        log.info("Fetching requests by userId={}, eventId={} and requestIds={}", userId, eventId, requestIds);
        EventFullDto event = eventClient.getPublicEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("User {} is not the owner of event {}", userId, eventId);
            throw new NotFoundException("User is not the owner of the event");
        }

        List<Request> requests = requestRepository.findAllById(requestIds);
        for (Request request : requests) {
            if (!request.getEventId().equals(eventId)) {
                log.warn("Request {} does not belong to event {}", request.getId(), eventId);
                throw new NotFoundException("Request does not belong to the specified event");
            }
        }

        List<RequestDto> result = requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
        log.debug("Returning {} matching requests", result.size());
        return result;
    }

    @Override
    public List<RequestDto> saveAll(List<RequestDto> requests) {
        log.info("Saving batch of {} requests", requests.size());
        List<Request> requestEntities = requests.stream().map(requestMapper::toEntity).toList();
        EventFullDto event = findEventById(requestEntities.getFirst().getEventId());

        int currentConfirmedRequests = event.getConfirmedRequests();
        int confirmedReq = (int) requestEntities.stream().filter(r -> r.getStatus() == RequestStatus.CONFIRMED).count();
        int notConfirmedReq = requestEntities.size() - confirmedReq;
        int confirmedRequests = currentConfirmedRequests + confirmedReq - notConfirmedReq;

        event.setConfirmedRequests(confirmedRequests);
        eventClient.updateUserEvent(event.getInitiator().getId(), event.getId(), eventMapper.toUpdateRequest(event));

        List<Request> savedRequests = requestRepository.saveAllAndFlush(requestEntities);
        List<RequestDto> result = savedRequests.stream().map(requestMapper::toDto).collect(Collectors.toList());

        log.info("Saved {} requests and updated event confirmed count to {}", result.size(), confirmedRequests);
        return result;
    }
}
