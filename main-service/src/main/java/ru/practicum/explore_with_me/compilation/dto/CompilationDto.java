package ru.practicum.explore_with_me.compilation.dto;

import ru.practicum.explore_with_me.event.dto.EventShortDto;

import java.util.List;

public record CompilationDto(
        List<EventShortDto> events,
        Long id,
        Boolean pinned,
        String title
) {
}
