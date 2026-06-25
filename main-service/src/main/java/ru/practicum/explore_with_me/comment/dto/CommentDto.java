package ru.practicum.explore_with_me.comment.dto;


import ru.practicum.explore_with_me.comment.model.CommentStatus;


import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        Long event,
        Long author,
        String text,
        CommentStatus status,
        LocalDateTime created,
        LocalDateTime updated
) {
}
