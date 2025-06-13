package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserRequestDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {

    @GetMapping("/multiple")
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @RequestParam("from") Integer from,
                           @RequestParam("size") Integer size);

    @GetMapping
    UserDto getUser(@RequestParam Long id);

    @PostMapping
    UserDto registerUser(@RequestBody UserRequestDto userRequestDto);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId);
}
