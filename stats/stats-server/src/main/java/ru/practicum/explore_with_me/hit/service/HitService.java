package ru.practicum.explore_with_me.hit.service;

import ru.practicum.explore_with_me.hit.dto.HitDto;
import ru.practicum.explore_with_me.hit.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    void addHit(HitDto hitDto);

    List<StatsDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
