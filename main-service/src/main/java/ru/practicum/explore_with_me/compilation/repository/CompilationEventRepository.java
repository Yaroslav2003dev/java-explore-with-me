package ru.practicum.explore_with_me.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.compilation.model.CompilationEvent;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent,Long> {
    List<CompilationEvent> findAllByCompilationId(Long id);
}
