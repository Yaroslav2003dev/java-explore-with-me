package ru.practicum.explore_with_me.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore_with_me.hit.model.Hit;
import ru.practicum.explore_with_me.hit.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("""
                select new ru.practicum.explore_with_me.hit.dto.StatsDto(h.app,h.uri,count(h.ip)) from Hit h where h.timestamp between :start and :end group by h.app, h.uri order by count(h.ip) desc
            """)
    List<StatsDto> findAllNoUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
                select new ru.practicum.explore_with_me.hit.dto.StatsDto(h.app, h.uri ,count(Distinct(h.ip))) from Hit h where h.timestamp between :start and :end group by h.app, h.uri order by count(Distinct(h.ip)) desc
            """)
    List<StatsDto> findAllUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
                select new ru.practicum.explore_with_me.hit.dto.StatsDto(h.app, h.uri, count(Distinct(h.ip))) from Hit h where h.uri in :listUri and h.timestamp between :start and :end group by h.app,h.uri order by count(Distinct(h.ip)) desc
            """)
    List<StatsDto> findAllByListUriUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("listUri") List<String> listUri);

    @Query("""
                select new ru.practicum.explore_with_me.hit.dto.StatsDto(h.app, h.uri, count(h.ip)) from Hit h where h.uri in :listUri and h.timestamp between :start and :end group by h.app,h.uri order by count(h.ip) desc
            """)
    List<StatsDto> findAllByListUriNoUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("listUri") List<String> listUri);
}
