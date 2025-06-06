package ru.practicum.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categories.model.Category;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
