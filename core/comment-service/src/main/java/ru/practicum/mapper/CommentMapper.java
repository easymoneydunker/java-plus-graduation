package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentDtoPublic;
import ru.practicum.model.Comment;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "create", ignore = true)
    Comment toComment(CommentDto commentDto);

    @Mapping(source = "eventId", target = "eventId")
    @Mapping(target = "create", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "eventId", target = "eventId")
    @Mapping(target = "create", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CommentDtoPublic toCommentDtoPublic(Comment comment);
}