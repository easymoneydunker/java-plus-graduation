package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoPublic;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto addCommentToEvent(Long authorId, Long eventId, CommentDto commentDto) {
        Comment comment = commentMapper.toComment(commentDto);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found."));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            comment.setAuthor(author);
            comment.setEvent(event);
            comment.setCreate(LocalDateTime.now());
            log.info("Add new comment for event id = {} and user with ID = {}.", eventId, authorId);
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            String message = MessageFormat.format("Event {0} has not published yet.", eventId);
            log.error(message);
            throw new ConflictException(message);
        }
    }

    @Override
    public CommentDto getCommentByUser(Long authorId, Long commentId) {
        log.info("Get comment with ID = {} for user with ID = {}.", commentId, authorId);
        return commentMapper.toCommentDto(getCommentById(commentId));
    }

    @Override
    public List<CommentDto> getAllCommentsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found."));
        if (event.getState().equals(EventState.PUBLISHED)) {
            List<Comment> comments = commentRepository.findAllByEventOrderByEvent(event);
            log.info("Get comment for event ID = {}.", eventId);
            return comments.stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
        } else {
            String message = MessageFormat.format("Event {0} has not published yet.", eventId);
            log.error(message);
            throw new ConflictException(message);
        }
    }

    @Override
    public CommentDto updateCommentByUser(Long authorId, Long commentId, CommentDto commentDto) {
        Comment comment = getCommentById(commentId);
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (comment.getAuthor().getId().equals(author.getId())) {
            comment.setText(commentDto.getText());
            comment.setCreate(LocalDateTime.now());
            log.info("Update comment with ID {}.", commentId);
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            String message = MessageFormat.format("The user ID {0} not the author of comment ID {1}.", authorId,
                    commentId);
            log.error(message);
            throw new ConflictException(message);
        }
    }

    @Override
    public void deleteCommentByUser(Long authorId, Long commentId) {
        Comment comment = getCommentById(commentId);
        if (comment.getAuthor().getId().equals(authorId)) {
            log.info("Delete comment with ID = {} by user ID {}.", commentId, authorId);
            commentRepository.deleteById(getCommentById(commentId).getId());
        } else {
            throw new ConflictException("The user is not the author of the comment.");
        }
    }

    @Override
    public CommentDto updateCommentByAdmin(Long commentId, CommentDto commentDto) {
        Comment comment = getCommentById(commentId);
        comment.setText(commentDto.getText());
        comment.setCreate(LocalDateTime.now());
        log.info("Update comment with ID= {} by admin.", commentId);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        log.info("Delete comment with ID = {} by admin.", commentId);
        commentRepository.deleteById(getCommentById(commentId).getId());
    }

    @Override
    public List<CommentDtoPublic> getAllCommentsByEventPublic(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found."));
        if (event.getState().equals(EventState.PUBLISHED)) {
            List<Comment> comments = commentRepository.findAllByEventOrderByEvent(event);
            log.info("Get public comment for event with ID = {}.", eventId);
            return comments.stream().map(commentMapper::toCommentDtoPublic).collect(Collectors.toList());
        } else {
            String message = MessageFormat.format("The event {0} has not been published yet. Commenting is not available.", eventId);
            log.error(message);
            throw new ConflictException(message);
        }
    }

    public Comment getCommentById(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            return commentOptional.get();
        }
        throw new NotFoundException("Comment with ID = " + commentId + " was not found.");
    }
}