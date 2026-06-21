package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import ru.practicum.explore_with_me.event.model.StateAction;

import java.time.LocalDateTime;

public record UpdateEventAdminRequest(
        @Size(min = 20, max = 2000)
        String annotation,
        Long category,
        @Size(min = 20, max = 7000)
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        LocationDto location,
        Boolean paid,
        Integer participantLimit,
        Boolean requestModeration,
        StateAction stateAction,
        @Size(min = 3, max = 120)
        String title
) {
}
