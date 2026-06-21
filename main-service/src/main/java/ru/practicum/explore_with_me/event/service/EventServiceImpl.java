package ru.practicum.explore_with_me.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.service.CategoryService;
import ru.practicum.explore_with_me.client.RequestClient;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.mapper.LocationMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.EventState;
import ru.practicum.explore_with_me.event.model.SortStatus;
import ru.practicum.explore_with_me.event.model.StateAction;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.exception.ValidationException;
import ru.practicum.explore_with_me.hit.dto.HitDto;
import ru.practicum.explore_with_me.hit.dto.StatsDto;
import ru.practicum.explore_with_me.request.StatusRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.mapper.ParticipationRequestMapper;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;
import ru.practicum.explore_with_me.request.model.QParticipationRequest;
import ru.practicum.explore_with_me.request.repository.ParticipationRequestRepository;
import ru.practicum.explore_with_me.request.services.ParticipationRequestService;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.explore_with_me.event.model.QEvent.event;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ParticipationRequestService participationRequestService;
    private final RequestClient requestClient;
    private final ParticipationRequestRepository participationRequestRepository;
    private static final String nameApp = "main-service";


    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        userService.getUserById(userId);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Predicate predicate = event.initiator.id.eq(userId);
        return eventRepository.findAll(predicate, pageable)
                .stream().map(EventMapper::toEventShortDto).toList();
    }


    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventRequest) {
        if (newEventRequest.getParticipantLimit() != null && newEventRequest.getParticipantLimit() < 0) {
            throw new ValidationException("Запрещено указывать отрицательный лимит участников");
        }
        LocalDateTime minDate = LocalDateTime.now().plusHours(2);
        User initiator = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(newEventRequest.getCategory());
        if (newEventRequest.getEventDate().isBefore(minDate)) {
            throw new ValidationException("Дата события должна быть не раньше, чем через два часа от текущего момента");
        }

        Event event = EventMapper.toEvent(newEventRequest, category);
        setViewsAndConfirmedRequests(List.of(event));
        event.setState(EventState.PENDING);
        event.setCreatedOn(minDate);
        event.setInitiator(initiator);
        event.setLocation(LocationMapper.toLocation(newEventRequest.getLocation()));
        if (newEventRequest.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEventByIdAndUserId(Long userId, Long eventId) {
        Event event = getEventByEventIdAndInitiatorId(eventId, userId);
        List<Event> events = List.of(event);
        event = setViewsAndConfirmedRequests(events).getFirst();
        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEventForUser(Long userId, Long eventId, UpdateEventUserRequest updateEventRequest) {
        if (updateEventRequest.participantLimit() != null && updateEventRequest.participantLimit() < 0) {
            throw new ValidationException("Запрещено указывать отрицательный лимит участников");
        }


        Event event = getEventByEventIdAndInitiatorId(eventId, userId);
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException(
                    "Изменить можно только отмененные события или события в состоянии ожидания модерации"
            );
        }
        Category category = null;
        if (updateEventRequest.category() != null) {
            category = categoryService.getCategoryById(updateEventRequest.category());
        }
        LocalDateTime eventDate = updateEventRequest.eventDate() != null
                ? updateEventRequest.eventDate()
                : event.getEventDate();


        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата события не может быть раньше, чем через два часа от текущего момента");
        }

        if (updateEventRequest.stateAction() != null) {
            updateStateForUser(event, updateEventRequest.stateAction());
        }
        EventMapper.updateEventRequestFieldsForUser(event, category, updateEventRequest);
        List<Event> events = List.of(event);
        event = setViewsAndConfirmedRequests(events).getFirst();
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestByEventIdAndUserId(Long userId, Long eventId) {
        userService.getUserById(userId);
        getEventByEventIdAndInitiatorId(eventId, userId);
        return participationRequestService.getRequestsForEvent(eventId);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userService.getUserById(userId);
        Event event = getEventByEventIdAndInitiatorId(eventId, userId);
        List<Event> events = setViewsAndConfirmedRequests(List.of(event));
        Event eventFull = events.getFirst();
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        if (eventFull.getParticipantLimit() == 0 || !eventFull.isRequestModeration()) {
            return result;
        }
        if (eventFull.getParticipantLimit() <= eventFull.getConfirmedRequests()) {
            throw new ConflictException("Закончились свободные места");
        }

        List<ParticipationRequest> requests =
                participationRequestService.findAllRequestByIds(eventRequestStatusUpdateRequest.getRequestIds());
        for (ParticipationRequest request : requests) {
            if (request.getStatusRequest() != StatusRequest.PENDING) {
                throw new ConflictException("Статус запроса не \"в ожидании\"");
            }
            if (eventRequestStatusUpdateRequest.getStatus() == StatusRequest.CONFIRMED) {


                if (eventFull.getParticipantLimit() <= eventFull.getConfirmedRequests()) {
                    request.setStatusRequest(StatusRequest.REJECTED);

                    result.getRejectedRequests()
                            .add(ParticipationRequestMapper.toParticipationRequestDto(request));

                    participationRequestService.updateRequest(
                            request.getId(),
                            StatusRequest.REJECTED
                    );
                    continue;
                }
                request.setStatusRequest(StatusRequest.CONFIRMED);

                result.getConfirmedRequests().add(ParticipationRequestMapper.toParticipationRequestDto(request));

                participationRequestService.updateRequest(request.getId(), StatusRequest.CONFIRMED);

                eventFull.setConfirmedRequests(
                        eventFull.getConfirmedRequests() + 1
                );
            } else {
                request.setStatusRequest(StatusRequest.REJECTED);
                result.getRejectedRequests().add(ParticipationRequestMapper.toParticipationRequestDto(request));
                participationRequestService.updateRequest(
                        request.getId(),
                        StatusRequest.REJECTED
                );
            }
        }
        return result;
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(
            List<Long> userIds,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size) {

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        BooleanBuilder builder = new BooleanBuilder();

        if (userIds != null && !userIds.isEmpty()) {
            builder.and(event.initiator.id.in(userIds));
        }

        if (categories != null && !categories.isEmpty()) {
            builder.and(event.category.id.in(categories));
        }

        if (states != null && !states.isEmpty()) {
            builder.and(event.state.in(states));
        }

        if (rangeStart != null) {
            builder.and(event.eventDate.goe(rangeStart));
        }

        if (rangeEnd != null) {
            builder.and(event.eventDate.loe(rangeEnd));
        }


        List<Event> events = eventRepository.findAll(builder, pageable)
                .toList();


        events = setViewsAndConfirmedRequests(events);

        List<EventFullDto> result =
                EventMapper.toEventFullDtos(events);

        log.info("Сформирован список событий из {} событий.", result.size());

        return result;
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getByEventId(eventId);
        Category category = null;
        if (updateEventAdminRequest.category() != null) {
            category = categoryService.getCategoryById(updateEventAdminRequest.category());
        }

        LocalDateTime eventDate = updateEventAdminRequest.eventDate() != null
                ? updateEventAdminRequest.eventDate()
                : event.getEventDate();

        if (event.getPublishedOn() != null &&
                eventDate
                        .isAfter(event.getPublishedOn().plusHours(1))) {
            throw new ConflictException(
                    "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации"
            );
        }
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Нельзя изменять событие, у которой дата события прошла");
        }

        if (updateEventAdminRequest.stateAction() != null) {
            updateStateForAdmin(event, updateEventAdminRequest.stateAction());
        }

        EventMapper.updateEventRequestFieldsForAdmin(event, category, updateEventAdminRequest);

        List<Event> events = List.of(event);
        events = setViewsAndConfirmedRequests(events);
        Event result = events.getFirst();
        log.info("Выполнено обновление события с ID = {}.", eventId);
        return EventMapper.toEventFullDto(result);
    }

    @Override
    public List<EventShortDto> getEventsForAll(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            SortStatus sort,
            Integer from,
            Integer size,
            HttpServletRequest httpServletRequest) {

        int page = from / size;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(event.state.eq(EventState.PUBLISHED));

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Дата окончания не может быть раньше начала");
        }


        if (text != null && !text.isBlank()) {
            String search = "%" + text.trim() + "%";

            builder.and(
                    event.annotation.likeIgnoreCase(search)
                            .or(event.description.likeIgnoreCase(search))
            );
        }


        if (categories != null && !categories.isEmpty()) {
            builder.and(event.category.id.in(categories));
        }


        if (paid != null) {
            builder.and(event.paid.eq(paid));
        }


        if (rangeStart != null) {
            builder.and(event.eventDate.goe(rangeStart));
        }

        if (rangeEnd != null) {
            builder.and(event.eventDate.loe(rangeEnd));
        }

        if (rangeStart == null && rangeEnd == null) {
            builder.and(event.eventDate.after(LocalDateTime.now()));
        }


        if (Boolean.TRUE.equals(onlyAvailable)) {

            QParticipationRequest request =
                    QParticipationRequest.participationRequest;

            JPQLQuery<Long> confirmedRequests =
                    JPAExpressions
                            .select(request.count())
                            .from(request)
                            .where(
                                    request.event.id.eq(event.id),
                                    request.statusRequest.eq(StatusRequest.CONFIRMED)
                            );

            builder.and(
                    event.participantLimit.eq(0)
                            .or(
                                    confirmedRequests.lt(
                                            event.participantLimit.longValue()
                                    )
                            )
            );
        }


        List<Event> events;


        if (sort.equals(SortStatus.EVENT_DATE)) {


            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(
                            Sort.Direction.ASC,
                            "eventDate"
                    )
            );


            events = eventRepository
                    .findAll(builder, pageable)
                    .toList();


        } else if (sort.equals(SortStatus.VIEWS)) {

            Pageable pageable = PageRequest.of(
                    0,
                    Integer.MAX_VALUE
            );

            events = eventRepository
                    .findAll(builder, pageable).getContent();

            events = setViewsAndConfirmedRequests(events);

            events.sort(
                    Comparator.comparing(
                            Event::getViews,
                            Comparator.nullsLast(Integer::compareTo)
                    ).reversed()
            );

            events = events.stream()
                    .skip(from)
                    .limit(size)
                    .toList();

        } else {

            throw new ValidationException(
                    String.format(
                            "Параметр сортировки имеет неверное значение = %s.",
                            sort
                    )
            );
        }


        events = setViewsAndConfirmedRequests(events);


        List<EventShortDto> result =
                EventMapper.toEventShortDtos(events);

        requestClient.addHit(
                new HitDto(
                        nameApp,
                        httpServletRequest.getRequestURI(),
                        httpServletRequest.getRemoteAddr(),
                        LocalDateTime.now()
                )
        );

        return result;
    }

    public EventFullDto getEventByIdForAll(Long id, HttpServletRequest httpServletRequest) {
        Predicate predicate = event.id.eq(id)
                .and(event.state.eq(EventState.PUBLISHED));
        Event event = eventRepository.findOne(predicate).orElseThrow(() -> new NotFoundException("Опубликованное событие с id = " + id + " не обнаружено"));

        requestClient.addHit(new HitDto(
                nameApp,
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.now()));

        Event eventFull = setViewsAndConfirmedRequests(List.of(event)).getFirst();


        return EventMapper.toEventFullDto(eventFull);
    }

    public List<Event> setViewsAndConfirmedRequests(List<Event> events) {
        List<StatsDto> stats = getViews(events);
        events = addViewsInEvent(events, stats);
        Map<Long, List<ParticipationRequest>> confirmedRequests = getConfirmedRequest(events);
        events = addConfirmedRequestsInEvent(events, confirmedRequests);
        return events;
    }

    public Event getEventByEventIdAndInitiatorId(Long eventId, Long userId) {
        Predicate predicate = event.id.eq(eventId)
                .and(event.initiator.id.eq(userId));
        return eventRepository.findOne(predicate).orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " инициатора с id " + userId + " не обнаружено"));

    }

    public Event getByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не обнаружено"));
        List<Event> eventList = setViewsAndConfirmedRequests(List.of(event));
        return eventList.getFirst();
    }

    public List<StatsDto> getViews(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> uris = new ArrayList<>();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(30);
        for (Event event : events) {
            uris.add("/events/" + event.getId());
        }
        ResponseEntity<List<StatsDto>> response =
                requestClient.getStat(start, end, uris, true);
        return response.getBody();
    }

    public List<Event> addViewsInEvent(List<Event> events, List<StatsDto> stats) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Long> views = new HashMap<>();
        if (stats != null) {
            for (StatsDto dto : stats) {
                String uri = dto.uri();

                if (uri.startsWith("/events/")) {
                    views.put(
                            Long.parseLong(uri.split("/")[2]),
                            dto.hits()
                    );
                }
            }
        }

        for (Event event : events) {
            event.setViews(
                    Math.toIntExact(views.getOrDefault(event.getId(), 0L))
            );
        }

        return events;
    }

    public Map<Long, List<ParticipationRequest>> getConfirmedRequest(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();
        List<ParticipationRequest> confirmedRequests =
                participationRequestRepository.findByEventIdInAndStatusRequest(ids, StatusRequest.CONFIRMED);
        Map<Long, List<ParticipationRequest>> result = new HashMap<>();

        for (ParticipationRequest request : confirmedRequests) {
            Long eventId = request.getEvent().getId();

            result.computeIfAbsent(eventId, k -> new ArrayList<>())
                    .add(request);
        }

        return result;
    }

    public List<Event> addConfirmedRequestsInEvent(List<Event> events, Map<Long, List<ParticipationRequest>> confirmedRequests) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }


        for (Event event : events) {
            List<ParticipationRequest> requests = confirmedRequests.getOrDefault(event.getId(), Collections.emptyList());
            event.setConfirmedRequests(requests.size());
        }
        return events;
    }

    private void updateStateForUser(Event event, StateAction action) {
        switch (action) {
            case CANCEL_REVIEW:
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException(
                            "Изменить можно только отмененные события или события в состоянии ожидания модерации"
                    );
                }
                event.setState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException(
                            "Изменить можно только отмененные события или события в состоянии ожидания модерации"
                    );
                }
                event.setState(EventState.PENDING);
                break;
            default:
                throw new ConflictException("У пользователя нет прав на выполнения " + action);
        }
    }

    private void updateStateForAdmin(Event event, StateAction action) {
        switch (action) {
            case PUBLISH_EVENT:
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException(
                            "Событие можно публиковать, только если оно в состоянии ожидания публикации"
                    );
                }

                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case REJECT_EVENT:
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException(
                            "Событие можно отклонить, только если оно еще не опубликовано"
                    );
                }
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new ConflictException("У пользователя нет прав на выполнения " + action);
        }
    }
}



