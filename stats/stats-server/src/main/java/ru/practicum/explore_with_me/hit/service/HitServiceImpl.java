package ru.practicum.explore_with_me.hit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.hit.dto.HitDto;
import ru.practicum.explore_with_me.hit.mapper.HitMapper;
import ru.practicum.explore_with_me.hit.model.Hit;
import ru.practicum.explore_with_me.hit.repository.HitRepository;
import ru.practicum.explore_with_me.hit.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Transactional
    @Override
    public void addHit(HitDto hitDto) {
        Hit hit = HitMapper.toHit(hitDto);
        hitRepository.save(hit);
    }

    @Override
    public List<StatsDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<StatsDto> statsList;
        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                statsList = hitRepository.findAllByListIpUniqueIp(start, end, uris);
            } else {
                statsList = hitRepository.findAllByListIpNoUniqueIp(start, end, uris);
            }
        } else {
            if (unique) {
                statsList = hitRepository.findAllUniqueIp(start, end);
            } else {
                statsList = hitRepository.findAllNoUniqueIp(start, end);
            }
        }
        return statsList;
    }
}
