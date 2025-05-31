package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoPublic;
import ru.practicum.comment.model.Comment;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "create", ignore = true)
    Comment toComment(CommentDto commentDto);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(target = "create", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(target = "create", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentDtoPublic toCommentDtoPublic(Comment comment);
}