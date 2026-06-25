package ru.practicum.explore_with_me.comment.mapper;

import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.dto.NewCommentRequest;
import ru.practicum.explore_with_me.comment.dto.UpdateCommentRequest;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.model.CommentStatus;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.model.User;

import java.util.List;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getEvent().getId(), comment.getAuthor().getId(), comment.getText(), comment.getStatus(), comment.getCreated(), comment.getUpdated());
    }

    public static Comment toComment(NewCommentRequest newCommentRequest, Event event, User author) {
        return Comment.builder()
                .event(event)
                .author(author)
                .text(newCommentRequest.getText())
                .status(CommentStatus.PUBLISHED)
                .build();
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> commentList) {
        return commentList.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    public static Comment updateCommentFields(Comment comment, UpdateCommentRequest updateCommentRequest) {
        if (updateCommentRequest.getText() != null && !updateCommentRequest.getText().isBlank()) {
            comment.setText(updateCommentRequest.getText());
        }
        return comment;
    }
}
