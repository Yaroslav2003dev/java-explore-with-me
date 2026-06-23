package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.event.model.EventState;
import ru.practicum.explore_with_me.user.dto.UserShortDto;

import java.time.LocalDateTime;

public record EventFullDto(

        String annotation,

        CategoryDto category,

        Integer confirmedRequests,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,

        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        Long id,

        UserShortDto initiator,

        LocationDto location,

        Boolean paid,

        Integer participantLimit,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,

        Boolean requestModeration,

        EventState state,

        String title,

        Integer views

) {}
