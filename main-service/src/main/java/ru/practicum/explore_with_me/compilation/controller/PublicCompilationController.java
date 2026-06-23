package ru.practicum.explore_with_me.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilationDtoList(@RequestParam(name = "pinned", required = false) Boolean pinned, @PositiveOrZero @RequestParam(defaultValue = "0", name = "from") Integer from, @Positive @RequestParam(defaultValue = "10", name = "size") Integer size) {
        return new ResponseEntity<>(compilationService.getCompilationDtoList(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationDto(@PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.getCompilationDtoById(compId), HttpStatus.OK);
    }
}
