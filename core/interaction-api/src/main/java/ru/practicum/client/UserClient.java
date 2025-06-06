package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserRequestDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {

    @GetMapping("/admin/users/multiple")
    List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                           @RequestParam(value = "from", defaultValue = "0") Integer from,
                           @RequestParam(value = "size", defaultValue = "10") Integer size);

    @GetMapping
    UserDto getUser(@RequestParam(value = "id") Long id);

    @PostMapping
    UserDto registerUser(@RequestBody UserRequestDto userRequestDto);

    @DeleteMapping("/{userId}")
    void delete(@PathVariable("userId") Long userId);
}
