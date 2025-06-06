package ru.practicum.dto.comment;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoPublic {
    private Long id;
    private String text;
    private String eventId;
    private String create;
}