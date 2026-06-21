package ru.practicum.explore_with_me.event.mapper;

import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto, Category category) {
        Event event = new Event();
        event.setEventDate(newEventDto.getEventDate());
        event.setCategory(category);
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setPaid(newEventDto.isPaid());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(event.getAnnotation(), CategoryMapper.toCategoryDto(event.getCategory()), event.getConfirmedRequests(), event.getCreatedOn(), event.getDescription(), event.getEventDate(),
                event.getId(), UserMapper.toUserShortDto(event.getInitiator()), LocationMapper.toLocationDto(event.getLocation()), event.isPaid(), event.getParticipantLimit(), event.getPublishedOn(), event.isRequestModeration(), event.getState(), event.getTitle(), event.getViews());
    }

    public static List<EventFullDto> toEventFullDtos(List<Event> events) {
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        for (Event event : events) {
            eventFullDtos.add(toEventFullDto(event));
        }
        return eventFullDtos;
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(event.getAnnotation(), CategoryMapper.toCategoryDto(event.getCategory()), event.getConfirmedRequests(), event.getEventDate(), event.getId(), UserMapper.toUserShortDto(event.getInitiator()), event.isPaid(), event.getTitle(), event.getViews());
    }

    public static List<EventShortDto> toEventShortDtos(List<Event> events) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            eventShortDtos.add(toEventShortDto(event));
        }
        return eventShortDtos;
    }

    public static Event updateEventRequestFieldsForUser(Event event, Category category, UpdateEventUserRequest updateEventRequest) {
        if (updateEventRequest.annotation() != null) {
            event.setAnnotation(updateEventRequest.annotation());
        }
        if (updateEventRequest.category() != null) {
            event.setCategory(category);
        }
        if (updateEventRequest.description() != null) {
            event.setDescription(updateEventRequest.description());
        }
        if (updateEventRequest.eventDate() != null) {
            event.setEventDate(updateEventRequest.eventDate());
        }
        if (updateEventRequest.location() != null) {
            event.setLocation(LocationMapper.toLocation(updateEventRequest.location()));
        }
        if (updateEventRequest.paid() != null) {
            event.setPaid(updateEventRequest.paid());
        }
        if (updateEventRequest.participantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.participantLimit());
        }
        if (updateEventRequest.requestModeration() != null) {
            event.setRequestModeration(updateEventRequest.requestModeration());
        }

        if (updateEventRequest.title() != null) {
            event.setTitle(updateEventRequest.title());
        }

        return event;
    }

    public static Event updateEventRequestFieldsForAdmin(
            Event event,
            Category category,
            UpdateEventAdminRequest updateEventRequest) {

        if (updateEventRequest.annotation() != null) {
            event.setAnnotation(updateEventRequest.annotation());
        }

        if (updateEventRequest.category() != null) {
            event.setCategory(category);
        }

        if (updateEventRequest.description() != null) {
            event.setDescription(updateEventRequest.description());
        }

        if (updateEventRequest.eventDate() != null) {

            event.setEventDate(updateEventRequest.eventDate());
        }

        if (updateEventRequest.location() != null) {
            event.setLocation(
                    LocationMapper.toLocation(updateEventRequest.location())
            );
        }

        if (updateEventRequest.paid() != null) {
            event.setPaid(updateEventRequest.paid());
        }

        if (updateEventRequest.participantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.participantLimit());
        }

        if (updateEventRequest.requestModeration() != null) {
            event.setRequestModeration(updateEventRequest.requestModeration());
        }

        if (updateEventRequest.title() != null) {
            event.setTitle(updateEventRequest.title());
        }

        return event;
    }

}
