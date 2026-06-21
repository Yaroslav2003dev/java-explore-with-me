package ru.practicum.explore_with_me.compilation.mapper;

import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationRequest;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore_with_me.compilation.model.Compilation;
import ru.practicum.explore_with_me.compilation.model.CompilationEvent;
import ru.practicum.explore_with_me.event.mapper.EventMapper;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationRequest newCompilationRequest) {
        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationRequest.getPinned() != null ? newCompilationRequest.getPinned() : false);
        compilation.setTitle(newCompilationRequest.getTitle());
        return compilation;
    }

    public static Compilation updateCompilationFields(UpdateCompilationRequest updateCompilationRequest, Compilation compilation) {
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        return compilation;
    }


    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(EventMapper.toEventShortDtos(compilation.getEvents().stream().map(CompilationEvent::getEvent).toList()), compilation.getId(), compilation.getPinned(), compilation.getTitle());
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilationList) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilationList) {
            compilationDtoList.add(toCompilationDto(compilation));
        }
        return compilationDtoList;
    }

}
