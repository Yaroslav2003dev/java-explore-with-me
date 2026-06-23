package ru.practicum.explore_with_me.compilation.service;

import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationRequest;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationRequest newCompilationRequest);

    void removeCompilation(Long id);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long id);

    List<CompilationDto> getCompilationDtoList(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationDtoById(Long compId);
}
