package ru.practicum.explore_with_me.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentDto;
import ru.practicum.explore_with_me.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
            @PathVariable Long commentId
    ) {
        return new ResponseEntity<>(commentService.getCommentById(commentId),HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByEventId(
            @RequestParam Long eventId,
            @PositiveOrZero @RequestParam(required = false, name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, name = "size", defaultValue = "10") Integer size
    ) {
        return new ResponseEntity<>(commentService.getCommentsByEventId(eventId, from, size),HttpStatus.OK);
    }

}
