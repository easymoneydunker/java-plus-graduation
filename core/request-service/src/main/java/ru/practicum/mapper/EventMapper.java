package ru.practicum.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "annotation", target = "annotation")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "paid", target = "paid")
    @Mapping(source = "participantLimit", target = "participantLimit")
    @Mapping(source = "requestModeration", target = "requestModeration")
    @Mapping(source = "title", target = "title")
    EventFullDto toFullDto(NewEventDto dto);

    @InheritInverseConfiguration
    @Mapping(target = "category", expression = "java(dto.getCategory().getId())")
    @Mapping(target = "location", source = "location")
    NewEventDto toNewEventDto(EventFullDto dto);

    @Mapping(source = "annotation", target = "annotation")
    @Mapping(source = "category.id", target = "category")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "paid", target = "paid")
    @Mapping(source = "participantLimit", target = "participantLimit")
    @Mapping(source = "requestModeration", target = "requestModeration")
    @Mapping(source = "title", target = "title")
    @Mapping(target = "stateAction", ignore = true)
    UpdateEventUserRequest toUpdateRequest(EventFullDto dto);
}
