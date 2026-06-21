package ru.practicum.explore_with_me.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.EventState;
import ru.practicum.explore_with_me.event.model.SortStatus;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEventRequest);

    EventFullDto getEventByIdAndUserId(Long userId, Long eventId);

    EventFullDto updateEventForUser(Long userId, Long eventId, UpdateEventUserRequest updateEventRequest);

    List<ParticipationRequestDto> getRequestByEventIdAndUserId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getEventsForAdmin(List<Long> userIds, List<EventState> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Integer from, Integer size);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEventsForAll(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            SortStatus sort,
            Integer from,
            Integer size,
            HttpServletRequest httpServletRequest);

    EventFullDto getEventByIdForAll(Long id, HttpServletRequest httpServletRequest);

    Event getByEventId(Long eventId);

    List<Event> setViewsAndConfirmedRequests(List<Event> events);
}
