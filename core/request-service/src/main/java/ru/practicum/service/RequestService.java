package ru.practicum.service;


import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatus;

import java.util.List;

public interface RequestService {
    List<RequestDto> getRequests(long userId, long eventId);

    List<RequestDto> getRequests(long userId);

    List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIdIn(long userId, long eventId, List<Long> requestIds);

    RequestDto createRequest(long userId, long eventId);

    List<RequestDto> saveAll(List<RequestDto> requests);

    RequestDto cancelRequest(long userId, long requestId);

    List<RequestDto> getConfirmedRequests(Long eventId, RequestStatus status);
}
