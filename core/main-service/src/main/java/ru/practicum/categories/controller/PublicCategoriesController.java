package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoriesService;

import java.util.List;

@Validated
@RequestMapping("/categories")
@RequiredArgsConstructor
@RestController
public class PublicCategoriesController {
    private final CategoriesService service;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return service.findBy(from, size);
    }

    //Получение категории по id
    @GetMapping("/{catId}")
    public CategoryDto getCategoryBy(@PathVariable Long catId) {
        return service.findBy(catId);
    }
}
