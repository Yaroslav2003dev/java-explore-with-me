package ru.practicum.explore_with_me.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.category.dto.UpdateCategoryRequest;
import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.repository.CategoryRepository;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryRequest) {
        if (categoryRepository.existsByName(newCategoryRequest.getName())) {
            throw new ConflictException("Категория с именем " + newCategoryRequest.getName() + " уже существует");
        }
        Category category = CategoryMapper.toCategory(newCategoryRequest);
        category = categoryRepository.save(category);
        log.info("Создана категория с id = " + category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("У категории с id = " + categoryId + " есть связанные события");
        }
        categoryRepository.delete(category);
        log.info("Удалена категория с id = " + categoryId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long categoryId, UpdateCategoryRequest updateUserRequest) {
        Category category = getCategoryById(categoryId);
        if (updateUserRequest.getName() != null
                && categoryRepository.existsByNameAndIdNot(
                updateUserRequest.getName(),
                categoryId)) {
            throw new ConflictException("Категория с именем " + updateUserRequest.getName() + " уже существует");
        }
        CategoryMapper.updatCategoryFields(category, updateUserRequest);
        log.info("Обновлена категория с id = " + categoryId);
        return CategoryMapper.toCategoryDto(category);
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Категория с id = " + categoryId + " отсутствует"));
    }

    public CategoryDto getCategoryDtoById(Long categoryId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Категория с id = " + categoryId + " отсутствует")));
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return categoryRepository.findAll(pageable).getContent().stream()
                .map(CategoryMapper::toCategoryDto).toList();
    }


}
