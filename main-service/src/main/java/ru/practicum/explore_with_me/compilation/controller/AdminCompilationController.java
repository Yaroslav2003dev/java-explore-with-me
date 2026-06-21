package ru.practicum.explore_with_me.compilation.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationRequest;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore_with_me.compilation.service.CompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> addCompilation(@Valid @RequestBody NewCompilationRequest newCompilationRequest) {
        return new ResponseEntity<>(compilationService.addCompilation(newCompilationRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compId) {
        compilationService.removeCompilation(compId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest, @PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.updateCompilation(updateCompilationRequest, compId), HttpStatus.OK);
    }


}
