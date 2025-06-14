package ru.practicum.event.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.ParticipationRequestDto;
import ru.practicum.dto.request.RequestDto;

@Mapper(componentModel = "spring")
public interface ReqMapper {

    @Mapping(target = "event", source = "event")
    @Mapping(target = "created", source = "created")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "status", source = "status")
    ParticipationRequestDto toParticipationRequestDto(RequestDto requestDto);

    @InheritInverseConfiguration
    RequestDto toRequestDto(ParticipationRequestDto participationRequestDto);
}
