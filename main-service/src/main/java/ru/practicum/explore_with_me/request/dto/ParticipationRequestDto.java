package ru.practicum.explore_with_me.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.explore_with_me.request.StatusRequest;

import java.time.LocalDateTime;

public record ParticipationRequestDto(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime created,
        Long event,
        Long id,
        Long requester,
        StatusRequest status
) {
}
