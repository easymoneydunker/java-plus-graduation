package ru.practicum.categories.mapper;

import org.mapstruct.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category toEntity(NewCategoryDto newCategoryDto);

    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Category update(@MappingTarget Category category, NewCategoryDto updateCategoryDto);
}
