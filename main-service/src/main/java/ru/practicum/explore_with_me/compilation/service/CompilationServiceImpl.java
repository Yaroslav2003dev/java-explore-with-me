package ru.practicum.explore_with_me.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationRequest;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore_with_me.compilation.mapper.CompilationMapper;
import ru.practicum.explore_with_me.compilation.model.Compilation;
import ru.practicum.explore_with_me.compilation.model.CompilationEvent;
import ru.practicum.explore_with_me.compilation.repository.CompilationRepository;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.exception.NotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final CompilationRepository compilationRepository;

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationRequest newCompilationRequest) {
        List<Event> events = eventRepository.findEvents(newCompilationRequest.getEvents());
        events = eventService.setViewsAndConfirmedRequests(events);
        Compilation compilation = CompilationMapper.toCompilation(newCompilationRequest);

        List<CompilationEvent> compilationEvents = events.stream()
                .map(event -> {
                    CompilationEvent ce = new CompilationEvent();
                    ce.setCompilation(compilation);
                    ce.setEvent(event);
                    return ce;
                })
                .toList();

        compilation.setEvents(compilationEvents);

        Compilation saved = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(saved);
    }

    @Transactional
    public void removeCompilation(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> new NotFoundException("Подборка с id = " + id + " не обнаружена"));
        compilationRepository.delete(compilation);
    }

    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> new NotFoundException("Подборка с id = " + id + " не обнаружена"));
        CompilationMapper.updateCompilationFields(updateCompilationRequest, compilation);
        if (updateCompilationRequest.getEvents() != null) {
            compilation.getEvents().clear();
            List<Event> eventList = eventRepository.findEvents(updateCompilationRequest.getEvents());
            eventService.setViewsAndConfirmedRequests(eventList);
            for (Event event : eventList) {
                CompilationEvent compilationEvent = new CompilationEvent();
                compilationEvent.setCompilation(compilation);
                compilationEvent.setEvent(event);
                compilation.getEvents().add(compilationEvent);
            }
        }
        Compilation saved = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(saved);
    }

    public List<CompilationDto> getCompilationDtoList(Boolean pinned, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        List<Compilation> compilationList;

        if (pinned == null) {
            compilationList = compilationRepository.findAll(pageable)
                    .getContent();
        } else {
            compilationList = compilationRepository
                    .findAllByPinned(pinned, pageable);
        }

        return CompilationMapper.toCompilationDtoList(compilationList);
    }

    public CompilationDto getCompilationDtoById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + " не найдена"));
        return CompilationMapper.toCompilationDto(compilation);
    }

}
