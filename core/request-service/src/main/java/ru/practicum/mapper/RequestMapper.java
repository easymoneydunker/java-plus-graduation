package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.model.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "eventId")
    @Mapping(target = "requester", source = "requesterId")
    RequestDto toDto(Request request);

    List<RequestDto> toDtos(List<Request> requests);

    @Mapping(target = "eventId", source = "event")
    @Mapping(target = "requesterId", source = "requester")
    Request toEntity(RequestDto requestDto);

    List<Request> toEntities(List<RequestDto> requestDtos);
}
