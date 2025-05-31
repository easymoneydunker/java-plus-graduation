package ru.practicum.categories.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoriesRepository;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.repository.EventRepository;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category entity = categoryMapper.toEntity(newCategoryDto);
        assertUniqueName(newCategoryDto, null);
        Category category = categoriesRepository.saveAndFlush(entity);
        log.info("Category is created: {}", category);
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long id, NewCategoryDto updateCategoryDto) {
        log.info("start updateCategory");
        Category category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
        assertUniqueName(updateCategoryDto, category);
        category = categoriesRepository.save(categoryMapper.update(category, updateCategoryDto));
        log.info("Category is updated: {}", category);
        return categoryMapper.toDto(categoriesRepository.save(category));
    }

    private void assertUniqueName(NewCategoryDto updateCategoryDto, Category category) {
        categoriesRepository.findByName(updateCategoryDto.getName())
                .ifPresent(cat -> {
                    if (category == null || !Objects.equals(cat.getId(), category.getId())) {
                        throw new ConflictException("Category with name {" +
                                                    updateCategoryDto.getName() +
                                                    "} already exist.");
                    }
                });
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        eventRepository.findAllByCategoryId(id).ifPresent(events -> {
            if (events.isEmpty()) {
                return;
            }
            throw new ConflictException("Category has this events: [" +
                                        (Arrays.toString(new List[]{events})) +
                                        "]. Change events before drop category.");
        });
        categoriesRepository.deleteById(id);
        log.info("Category deleted with id: {}", id);
    }

    @Override
    public CategoryDto findBy(Long id) {
        log.info("Starting to retrieve category by id: {}", id);

        return categoriesRepository.findById(id)
                .map(category -> {
                    log.info("Category is found: {}", category);
                    return categoryMapper.toDto(category);
                })
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    }

    @Override
    public List<CategoryDto> findBy(int from, int size) {
        log.info("start getCategory by from {} to {}", from, size);
        return categoriesRepository.findAll(PageRequest.of(from > 0 ? from / size : 0, size))
                .stream()
                .map(categoryMapper::toDto).toList();
    }

    @Override
    public Category getOrThrow(Long id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));

    }
}
