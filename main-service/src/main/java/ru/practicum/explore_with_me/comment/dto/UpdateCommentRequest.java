package ru.practicum.explore_with_me.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCommentRequest {
    @Size(max = 2000)
    private String text;
}
