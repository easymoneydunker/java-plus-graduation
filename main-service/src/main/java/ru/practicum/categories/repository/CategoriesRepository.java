package ru.practicum.categories.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categories.model.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
