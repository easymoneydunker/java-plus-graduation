package ru.practicum.event.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    ru.practicum.event.model.Location toEntity(ru.practicum.dto.event.Location dto);

    ru.practicum.dto.event.Location toDto(ru.practicum.event.model.Location entity);
}

