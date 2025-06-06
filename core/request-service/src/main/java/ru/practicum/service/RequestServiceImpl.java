package ru.practicum.service;

import lombok.RequiredArgsConstructor;
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
        UserDto user = findUserById(userId);
        return requestMapper.toDtos(requestRepository.findByRequesterId(user.getId()));
    }

    @Override
    public RequestDto createRequest(long userId, long eventId) {
        EventFullDto event = findEventById(eventId);
        UserDto user = findUserById(userId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exist");
        }

        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException("Request can't be created by initiator");
        }

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Event not yet published");
        }

        int requestsSize = requestRepository.findAllByEventId(eventId).size();
        if (event.getParticipantLimit() > 0 && !event.isRequestModeration() && event.getParticipantLimit() <= requestsSize) {
            throw new ConflictException("Participant limit exceeded");
        }

        Request eventRequest = new Request(null, LocalDateTime.now(), eventId, userId, RequestStatus.PENDING);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            eventRequest.setStatus(RequestStatus.CONFIRMED);
        }

        if (eventRequest.getStatus() == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventClient.updateUserEvent(userId, eventId, eventMapper.toUpdateRequest(event));
        }

        return requestMapper.toDto(requestRepository.save(eventRequest));
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("Request with id={0} was not found", requestId)));

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getConfirmedRequests(Long eventId, RequestStatus status) {
        return requestRepository.findAllByEventIdAndStatus(eventId, status)
                .stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    private UserDto findUserById(long userId) {
        return userClient.getUser(userId);
    }

    private EventFullDto findEventById(long eventId) {
        return eventClient.getPublicEventById(eventId);
    }

    @Override
    public List<RequestDto> getRequests(long userId, long eventId) {
        EventFullDto event = findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("User is not the owner of the event");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIdIn(long userId, long eventId,
                                                                        List<Long> requestIds) {
        EventFullDto event = eventClient.getPublicEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("User is not the owner of the event");
        }

        List<Request> requests = requestRepository.findAllById(requestIds);

        for (Request request : requests) {
            if (!request.getEventId().equals(eventId)) {
                throw new NotFoundException("Request does not belong to the specified event");
            }
        }

        return requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> saveAll(List<RequestDto> requests) {
        List<Request> requestEntities = requests.stream().map(requestMapper::toEntity).toList();
        EventFullDto event = findEventById(requestEntities.getFirst().getEventId());

        int currentConfirmedRequests = event.getConfirmedRequests();
        int confirmedReq = (int) requestEntities.stream().filter(r -> r.getStatus() == RequestStatus.CONFIRMED).count();
        int notConfirmedReq = requestEntities.size() - confirmedReq;
        int confirmedRequests = currentConfirmedRequests + confirmedReq - notConfirmedReq;
        event.setConfirmedRequests(confirmedRequests);
        eventClient.updateUserEvent(event.getInitiator().getId(), event.getId(), eventMapper.toUpdateRequest(event));
        List<Request> savedRequests = requestRepository.saveAllAndFlush(requestEntities);

        return savedRequests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }
}
