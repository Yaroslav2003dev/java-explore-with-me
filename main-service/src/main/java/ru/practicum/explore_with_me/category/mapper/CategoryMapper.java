package ru.practicum.explore_with_me.category.mapper;

import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.category.dto.UpdateCategoryRequest;
import ru.practicum.explore_with_me.category.model.Category;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(),
                category.getName());
    }

    public static Category toCategory(NewCategoryDto newCategoryRequest) {
        Category category = new Category();
        category.setName(newCategoryRequest.getName());
        return category;
    }

    public static Category updatCategoryFields(Category category, UpdateCategoryRequest updateCategoryRequest) {
        if (updateCategoryRequest.getName() != null && !updateCategoryRequest.getName().isBlank()) {
            category.setName(updateCategoryRequest.getName());
        }
        return category;
    }
}
