package ru.practicum.explore_with_me.hit.dto;

public record StatsDto(
        String app,
        String uri,
        Long hits
) {
}
