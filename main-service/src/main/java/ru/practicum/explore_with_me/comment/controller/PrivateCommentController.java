package ru.practicum.explore_with_me.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.dto.NewCommentRequest;
import ru.practicum.explore_with_me.comment.dto.UpdateCommentRequest;
import ru.practicum.explore_with_me.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getMyComments(@PathVariable Long userId,
                                          @PositiveOrZero @RequestParam(required = false, name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(required = false, name = "size", defaultValue = "10") Integer size) {
        return new ResponseEntity<>(commentService.getMyComments(userId, from, size),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long userId,
            @RequestBody NewCommentRequest dto
    ) {
        return new ResponseEntity<>(commentService.createComment(userId, dto),HttpStatus.CREATED);
    }


    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest dto
    ) {
        return new ResponseEntity<>(commentService.updateComment(
                userId,
                commentId,
                dto
        ),HttpStatus.OK);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(
                userId,
                commentId
        );
        return ResponseEntity.noContent().build();
    }
}
