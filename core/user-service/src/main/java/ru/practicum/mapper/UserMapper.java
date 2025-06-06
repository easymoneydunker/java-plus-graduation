package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserRequestDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestDto dto);

    UserDto toDto(User entity);

    User toEntity(UserDto dto);

    UserShortDto toShortDto(User entity);
}
