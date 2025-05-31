package ru.practicum.stat.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.server.model.EndpointHitEntity;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHit toDto(EndpointHitEntity entity);

    @Mapping(target = "id", ignore = true)
    EndpointHitEntity toEntity(EndpointHit dto);
}
