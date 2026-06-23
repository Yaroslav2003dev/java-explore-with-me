package ru.practicum.explore_with_me.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.model.SortStatus;
import ru.practicum.explore_with_me.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@RequestParam(required = false, name = "text") String text,
                                                                    @RequestParam(required = false, name = "categories") List<Long> categories,
                                                                    @RequestParam(required = false, name = "paid") Boolean paid,
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false, name = "rangeStart") LocalDateTime rangeStart,
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false, name = "rangeEnd") LocalDateTime rangeEnd,
                                                                    @RequestParam(required = false, name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                                    @RequestParam(required = false, name = "sort", defaultValue = "EVENT_DATE") SortStatus sort,
                                                                    @PositiveOrZero @RequestParam(required = false, name = "from", defaultValue = "0") Integer from,
                                                                    @Positive @RequestParam(required = false, name = "size", defaultValue = "10") Integer size,
                                                                    HttpServletRequest request
    ) {
        return new ResponseEntity<>(eventService.getEventsForAll(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long id, HttpServletRequest request) {
        return new ResponseEntity<>(eventService.getEventByIdForAll(id, request), HttpStatus.OK);
    }
}
