package ru.practicum.service;


import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserRequestDto;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {
    UserDto getUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto registerUser(UserRequestDto userRequestDto);

    void delete(Long userId);

    User getOrThrow(Long id);
}
