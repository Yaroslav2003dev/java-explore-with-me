package ru.practicum.explore_with_me.category.contoller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0", name = "from") Integer from, @Positive @RequestParam(defaultValue = "10", name = "size") Integer size) {
        return new ResponseEntity<>(categoryService.getCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long catId) {
        return new ResponseEntity<>(categoryService.getCategoryDtoById(catId), HttpStatus.OK);
    }
}
