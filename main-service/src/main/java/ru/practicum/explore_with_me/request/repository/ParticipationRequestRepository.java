package ru.practicum.explore_with_me.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.request.StatusRequest;
import ru.practicum.explore_with_me.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByEventIdInAndStatusRequest(
            List<Long> eventId,
            StatusRequest statusRequest
    );

    List<ParticipationRequest> findAllByEventId(
            Long eventId
    );

    List<ParticipationRequest> findAllByRequesterIdAndEventInitiatorIdNot(
            Long requesterId,
            Long initiatorId
    );

    Optional<ParticipationRequest> findByEventIdAndRequesterId(
            Long eventId,
            Long userId
    );

    Long countByEventIdAndStatusRequest(Long eventId, StatusRequest statusRequest);
}
