package ru.practicum.feign.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.feign.config.FeignClientConfig;

import java.util.List;

@FeignClient(name = "request-service", configuration = FeignClientConfig.class)
@Headers("feign-request: true")
public interface RequestClient {
    @GetMapping("/users/{userId}/requests")
    List<RequestDto> getRequests(@PathVariable("userId") long userId,
                                 @RequestParam(required = false) Long eventId);

    @GetMapping("/users/{userId}/requests/event")
    List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIdIn(@PathVariable("userId") long userId,
                                                                 @RequestParam long eventId,
                                                                 @RequestParam List<Long> requestIds);

    @GetMapping("/users/{userId}/requests/confirmed")
    List<RequestDto> getConfirmedRequests(@PathVariable("userId") long userId,
                                          @RequestParam long eventId,
                                          @RequestParam RequestStatus requestStatus);

    @PutMapping("/users/{userId}/requests")
    List<RequestDto> saveAll(@PathVariable("userId") long userId,
                             @RequestBody List<RequestDto> requestDtoList);
}
