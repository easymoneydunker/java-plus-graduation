package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> getRequests(@PathVariable long userId,
                                        @RequestParam(required = false) Long eventId) {
        if (eventId != null) {
            return requestService.getRequests(userId, eventId);
        }
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable long userId,
                                    @RequestParam long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto requestCancel(@PathVariable long requestId,
                                    @PathVariable long userId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public RequestDto confirmRequest(@PathVariable long userId,
                                     @PathVariable long eventId,
                                     @RequestBody RequestDto dto) {
        return requestService.confirmRequest(userId, eventId, dto);
    }


    @PutMapping
    public List<RequestDto> saveAll(@RequestBody List<RequestDto> requestDtoList) {
        return requestService.saveAll(requestDtoList);
    }

    @GetMapping("/event")
    List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIdIn(@PathVariable long userId,
                                                                 @RequestParam long eventId,
                                                                 @RequestParam List<Long> requestIds) {
        return requestService.getRequestsByUserIdAndEventIdAndRequestIdIn(userId, eventId, requestIds);
    }

    @GetMapping("/confirmed")
    List<RequestDto> getConfirmedRequests(@PathVariable long userId,
                                          @RequestParam long eventId,
                                          @RequestParam RequestStatus requestStatus) {
        return requestService.getConfirmedRequests(eventId, requestStatus);
    }

}
