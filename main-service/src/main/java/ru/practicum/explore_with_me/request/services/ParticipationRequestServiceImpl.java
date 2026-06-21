package ru.practicum.explore_with_me.request.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.EventState;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.request.StatusRequest;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.mapper.ParticipationRequestMapper;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;
import ru.practicum.explore_with_me.request.repository.ParticipationRequestRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsForEvent(Long eventId) {
        List<ParticipationRequest> participationRequestList = participationRequestRepository.findAllByEventId(eventId);
        return ParticipationRequestMapper.toParticipationRequestDtoList(participationRequestList);
    }

    public List<ParticipationRequest> findAllRequestByIds(List<Long> requestIds) {
        return participationRequestRepository.findAllById(requestIds);
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateRequest(Long idRequest, StatusRequest status) {
        ParticipationRequest request = participationRequestRepository.findById(idRequest).orElseThrow(() -> new NotFoundException("Заявка с id " + idRequest + " не найдена"));
        request.setStatusRequest(status);
        ParticipationRequest result = participationRequestRepository.save(request);
        return ParticipationRequestMapper.toParticipationRequestDto(result);
    }

    public List<ParticipationRequestDto> getAllRequestByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не обнаружен"));
        List<ParticipationRequest> requests = participationRequestRepository.findAllByRequesterIdAndEventInitiatorIdNot(userId, userId);
        return ParticipationRequestMapper.toParticipationRequestDtoList(requests);
    }

    @Transactional
    public ParticipationRequestDto addRequest(Long eventId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не обнаружен"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не обнаружено"));
        if (participationRequestRepository
                .findByEventIdAndRequesterId(eventId, userId)
                .isPresent()) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        long confirmed =
                participationRequestRepository
                        .countByEventIdAndStatusRequest(eventId, StatusRequest.CONFIRMED);
        if (event.getParticipantLimit() != 0
                && confirmed >= event.getParticipantLimit()) {
            throw new ConflictException("У события достигнут лимит запросов на участие ");
        }
        ParticipationRequest participationRequestNew = new ParticipationRequest();
        if (event.isRequestModeration()) {
            participationRequestNew.setStatusRequest(StatusRequest.PENDING);
        } else {
            participationRequestNew.setStatusRequest(StatusRequest.CONFIRMED);
        }
        if (event.getParticipantLimit() == 0) {
            participationRequestNew.setStatusRequest(StatusRequest.CONFIRMED);
        }

        participationRequestNew.setRequester(user);
        participationRequestNew.setCreated(LocalDateTime.now());
        participationRequestNew.setEvent(event);
        participationRequestRepository.save(participationRequestNew);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestNew);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не обнаружен"));
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));
        if (!participationRequest.getRequester().getId().equals(userId)) {
            throw new ConflictException("Пользователь с id = " + userId + " не является инициатором запроса с id = " + requestId);
        }
        participationRequest.setStatusRequest(StatusRequest.CANCELED);
        participationRequestRepository.save(participationRequest);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }


}
