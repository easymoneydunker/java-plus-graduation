package ru.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserRequestDto;
import ru.practicum.feign.config.FeignClientConfig;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @RequestParam("from") Integer from,
                           @RequestParam("size") Integer size);

    @GetMapping("/user")
    UserDto getUser(@RequestParam Long id);

    @PostMapping
    UserDto registerUser(@RequestBody UserRequestDto userRequestDto);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId);
}
