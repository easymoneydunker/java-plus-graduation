package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoPublic;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public ResponseEntity<CommentDto> addCommentToEvent(@PathVariable("userId") long authorId,
                                                        @PathVariable("eventId") long eventId,
                                                        @Valid @RequestBody CommentDto commentDto) {
        log.info("Call addCommentToEvent with authorId={}, eventId={}", authorId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addCommentToEvent(authorId, eventId, commentDto));
    }

    @GetMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> getCommentByUser(@PathVariable("userId") long authorId,
                                                       @PathVariable("commentId") long commentId) {
        log.info("Call getCommentByUser with authorId = {} and commentId = {}", authorId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentByUser(authorId, commentId));
    }

    @GetMapping("user/events/{eventId}/comments")
    public ResponseEntity<List<CommentDto>> getAllCommentsByEvent(@PathVariable("eventId") long eventId) {
        log.info("Call getAllCommentsByEvent eventId = {}", eventId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsByEvent(eventId));
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateCommentByUser(@PathVariable("userId") long authorId,
                                                          @PathVariable("commentId") long commentId,
                                                          @Valid @RequestBody CommentDto commentDto) {
        log.info("Call updateCommentByUser authorId = {} commentId = {}", authorId, commentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.updateCommentByUser(authorId, commentId, commentDto));
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable("userId") long authorId,
                                    @PathVariable("commentId") long commentId) {
        log.info("Call deleteCommentByUser userId = {}; commentId  = {}", commentId, authorId);
        commentService.deleteCommentByUser(authorId, commentId);
    }

    @PatchMapping("admin/comments/{commentId}")
    public ResponseEntity<CommentDto> updateCommentByAdmin(@PathVariable("commentId") long commentId,
                                                           @Valid @RequestBody CommentDto commentDto) {
        log.info("Call updateCommentByAdmin with commentId = {}.", commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateCommentByAdmin(commentId, commentDto));
    }

    @DeleteMapping("admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable("commentId") long commentId) {
        log.info("Call deleteCommentByAdmin with commentId = {}.", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("events/{eventId}/comments")
    public ResponseEntity<List<CommentDtoPublic>> getAllCommentsByEventPublic(@PathVariable("eventId") long eventId) {
        log.info("Call getAllCommentsByEventPublic with eventId = {}.", eventId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsByEventPublic(eventId));
    }
}