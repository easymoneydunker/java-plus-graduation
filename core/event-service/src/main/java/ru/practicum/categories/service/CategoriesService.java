package ru.practicum.categories.service;


import ru.practicum.categories.model.Category;
import ru.practicum.dto.categories.CategoryDto;
import ru.practicum.dto.categories.NewCategoryDto;

import java.util.List;

public interface CategoriesService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long id, NewCategoryDto newCategoryDto);

    void deleteCategory(Long id);

    CategoryDto findBy(Long id);

    List<CategoryDto> findBy(int from, int size);

    Category getOrThrow(Long id);
}
