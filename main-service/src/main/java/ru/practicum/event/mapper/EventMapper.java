package ru.practicum.event.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "eventDate", expression = "java(event.getEventDate())")
    @Mapping(target = "views", expression = "java(event.getViews() == null ? 0 : event.getViews().size())")
    EventShortDto toShortDto(Event event);

    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "views", expression = "java(event.getViews() == null ? 0 : event.getViews().size())")
    EventFullDto toFullDto(Event event);

    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(ru.practicum.event.model.EventState.PENDING)")
    @Mapping(target = "participantLimit", source = "participantLimit", defaultValue = "0")
    Event toEntity(NewEventDto newEventDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "category")
    void updateFromAdminRequest(UpdateEventAdminRequest updateRequest, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "category")
    void updateFromUserRequest(UpdateEventUserRequest updateRequest, @MappingTarget Event event);
}
