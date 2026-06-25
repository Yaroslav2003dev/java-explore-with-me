package ru.practicum.explore_with_me.comment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NewCommentRequest {
    @NotNull
    private Long event;
    @NotBlank
    @Size(max = 2000)
    private String text;
}
