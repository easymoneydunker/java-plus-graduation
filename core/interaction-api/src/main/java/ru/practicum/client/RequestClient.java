package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatus;

import java.util.List;

@FeignClient(name = "request-service", path = "/users/{userId}/requests")
public interface RequestClient {
    @GetMapping
    List<RequestDto> getRequests(@PathVariable long userId,
                                 @RequestParam(required = false) long eventId);

    @GetMapping("/users/{userId}/requests/event")
    List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIdIn(@PathVariable long userId,
                                                                 @RequestParam long eventId,
                                                                 @RequestParam List<Long> requestIds);

    @GetMapping("/users/{userId}/requests/confirmed")
    List<RequestDto> getConfirmedRequests(@PathVariable long userId,
                                          @RequestParam long eventId,
                                          RequestStatus requestStatus);

    @PutMapping
    List<RequestDto> saveAll(@RequestBody List<RequestDto> requestDtoList);
}