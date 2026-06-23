package ru.practicum.explore_with_me.category.service;

import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.category.dto.UpdateCategoryRequest;
import ru.practicum.explore_with_me.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryRequest);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, UpdateCategoryRequest updateUserRequest);

    CategoryDto getCategoryDtoById(Long categoryId);

    Category getCategoryById(Long categoryId);

    List<CategoryDto> getCategories(Integer from, Integer size);
}
