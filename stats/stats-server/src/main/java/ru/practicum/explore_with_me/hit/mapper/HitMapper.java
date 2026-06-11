package ru.practicum.explore_with_me.hit.mapper;

import ru.practicum.explore_with_me.hit.dto.HitDto;
import ru.practicum.explore_with_me.hit.model.Hit;

public class HitMapper {
    public static Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setApp(hitDto.getApp());
        hit.setTimestamp(hitDto.getTimestamp());
        return hit;
    }
}
