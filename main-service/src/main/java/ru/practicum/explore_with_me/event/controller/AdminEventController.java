package ru.practicum.explore_with_me.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore_with_me.event.model.EventState;
import ru.practicum.explore_with_me.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEventFullDtoList(@RequestParam(required = false, name = "users") List<Long> users,
                                                                  @RequestParam(required = false, name = "states") List<EventState> states,
                                                                  @RequestParam(required = false, name = "categories") List<Long> categories,
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false, name = "rangeStart") LocalDateTime rangeStart,
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false, name = "rangeEnd") LocalDateTime rangeEnd,
                                                                  @PositiveOrZero @RequestParam(required = false, name = "from", defaultValue = "0") Integer from,
                                                                  @Positive @RequestParam(required = false, name = "size", defaultValue = "10") Integer size
    ) {
        return new ResponseEntity<>(eventService.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return new ResponseEntity<>(eventService.updateEventAdmin(eventId, updateEventAdminRequest), HttpStatus.OK);
    }
}
