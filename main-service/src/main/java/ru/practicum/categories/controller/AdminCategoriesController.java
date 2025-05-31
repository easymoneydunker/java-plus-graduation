package ru.practicum.categories.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.service.CategoriesService;

@Validated
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@RestController
public class AdminCategoriesController {
    private final CategoriesService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return service.addCategory(newCategoryDto);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable("id") Long id, @Valid @RequestBody NewCategoryDto newCategoryDto) {
        return service.updateCategory(id, newCategoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable("id") Long id) {
        service.deleteCategory(id);
    }
}
