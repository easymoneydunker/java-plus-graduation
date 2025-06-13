package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.dto.event.*;
import ru.practicum.event.model.Event;
import ru.practicum.feign.client.UserClient;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "eventDate", expression = "java(event.getEventDate())")
    @Mapping(target = "views", expression = "java(event.getViews() == null ? 0 : event.getViews().size())")
    EventShortDto toShortDto(Event event);

    @Mapping(
            target = "initiator",
            expression = "java(userClient.getUser(event.getUserId()))"
    )
    @Mapping(target = "views", expression = "java(event.getViews() == null ? 0 : event.getViews().size())")
    EventFullDto toFullDto(Event event, @Context UserClient userClient);

    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(ru.practicum.dto.event.EventState.PENDING)")
    @Mapping(target = "participantLimit", source = "participantLimit", defaultValue = "0")
    Event toEntity(NewEventDto newEventDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "category")
    void updateFromAdminRequest(UpdateEventAdminRequest updateRequest, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "category")
    void updateFromUserRequest(UpdateEventUserRequest updateRequest, @MappingTarget Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "initiator", ignore = true)
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
}
