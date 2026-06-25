package ru.practicum.explore_with_me.comment.service;

import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.dto.NewCommentRequest;
import ru.practicum.explore_with_me.comment.dto.UpdateCommentRequest;
import ru.practicum.explore_with_me.comment.model.CommentStatus;

import java.util.List;

public interface CommentService {
    CommentDto getCommentById(Long id);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    List<CommentDto> getMyComments(Long userId, Integer from, Integer size);

    CommentDto createComment(Long userId, NewCommentRequest dto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> getAllComment(Long userId, Long eventId, CommentStatus status, Integer from, Integer size);
}
