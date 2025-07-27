// src/main/java/com/example/backend/service/CategoryService.java
package com.example.backend.service;

import com.example.backend.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    void deleteById(Long id);
    List<Category> findByName(String name);
}
