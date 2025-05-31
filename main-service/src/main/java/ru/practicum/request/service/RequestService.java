package ru.practicum.request.service;

import java.util.List;

import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

public interface RequestService {
    List<RequestDto> getRequests(long userId, long eventId);

    List<RequestDto> getRequests(long userId);

    List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIdIn(long userId, long eventId, List<Long> requestIds);

    RequestDto createRequest(long userId, long eventId);

    List<RequestDto> saveAll(List<RequestDto> requests);

    RequestDto cancelRequest(long userId, long requestId);

    List<Request> getConfirmedRequests(Long eventId, RequestStatus status);
}
