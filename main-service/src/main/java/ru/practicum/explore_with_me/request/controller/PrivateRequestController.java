package ru.practicum.explore_with_me.request.controller;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.services.ParticipationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final ParticipationRequestService participationRequestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getParticipationRequestDto(@PathVariable Long userId) {
        return new ResponseEntity<>(participationRequestService.getAllRequestByUserId(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addParticipationRequest(@PathVariable Long userId, @PositiveOrZero @RequestParam(required = true, name = "eventId") Long eventId) {
        return new ResponseEntity<>(participationRequestService.addRequest(eventId, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelParticipationRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return new ResponseEntity<>(participationRequestService.cancelRequest(userId, requestId), HttpStatus.OK);
    }

}
