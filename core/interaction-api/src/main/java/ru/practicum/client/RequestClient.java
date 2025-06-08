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
                                 @RequestParam(required = false) Long eventId);

    @GetMapping("/event")
    List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIdIn(@PathVariable long userId,
                                                                 @RequestParam long eventId,
                                                                 @RequestParam List<Long> requestIds);

    @GetMapping("/confirmed")
    List<RequestDto> getConfirmedRequests(@PathVariable long userId,
                                          @RequestParam long eventId,
                                          @RequestParam RequestStatus requestStatus);

    @PutMapping
    List<RequestDto> saveAll(@PathVariable long userId,
                             @RequestBody List<RequestDto> requestDtoList);
}
