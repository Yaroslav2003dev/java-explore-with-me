package ru.practicum.explore_with_me.user.dto;

import jakarta.validation.constraints.Size;

public record UserDto(
        @Size(min = 6, max = 254)
        String email,
        Long id,
        @Size(min = 2, max = 250)
        String name
) {
}
