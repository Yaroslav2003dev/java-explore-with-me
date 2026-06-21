package ru.practicum.explore_with_me.request.services;

import ru.practicum.explore_with_me.request.StatusRequest;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getRequestsForEvent(Long eventId);

    List<ParticipationRequest> findAllRequestByIds(List<Long> requestIds);

    ParticipationRequestDto updateRequest(Long idRequest, StatusRequest status);

    List<ParticipationRequestDto> getAllRequestByUserId(Long userId);

    ParticipationRequestDto addRequest(Long eventId, Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
