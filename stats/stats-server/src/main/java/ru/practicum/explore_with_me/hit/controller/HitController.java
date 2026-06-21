package ru.practicum.explore_with_me.hit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.hit.dto.HitDto;
import ru.practicum.explore_with_me.hit.dto.StatsDto;
import ru.practicum.explore_with_me.hit.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    public ResponseEntity<HitDto> addHit(@Valid @RequestBody HitDto hitDto) {
        hitService.addHit(hitDto);
        return new ResponseEntity<>(hitDto, HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsDto>> getStat(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")@RequestParam("start") LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("end") LocalDateTime end, @RequestParam(value = "uris",required = false) List<String> uris, @RequestParam(defaultValue = "false", name = "unique") boolean unique) {
        return new ResponseEntity<>(hitService.getHits(start,end,uris,unique), HttpStatus.OK);
    }
}
