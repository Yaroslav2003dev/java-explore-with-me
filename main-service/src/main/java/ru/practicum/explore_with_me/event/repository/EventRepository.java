package ru.practicum.explore_with_me.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore_with_me.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("""
                select e
                from Event e
                where e.id in :eventIds
            """)
    List<Event> findEvents(@Param("eventIds") List<Long> eventIds);

    boolean existsByCategoryId(Long categoryId);


}
