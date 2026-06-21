package ru.practicum.explore_with_me.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank
    @Size(max = 50)
    private String name;
}
