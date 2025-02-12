package org.wildcodeschool.myblog.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wildcodeschool.myblog.model.Category;

import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}
