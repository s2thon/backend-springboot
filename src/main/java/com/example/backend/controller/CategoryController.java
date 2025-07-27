package com.example.backend.controller;

import com.example.backend.dto.CategoryDto;
import com.example.backend.entity.Category;
import com.example.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> listAll() {
        return categoryService.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        return categoryService.findById(id)
            .map(c -> ResponseEntity.ok(toDto(c)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public CategoryDto create(@RequestBody CategoryDto dto) {
        Category saved = categoryService.save(toEntity(dto));
        return toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id,
                                              @RequestBody CategoryDto dto) {
        return categoryService.findById(id)
            .map(existing -> {
                Category toSave = toEntity(dto);
                toSave.setId(id);
                return ResponseEntity.ok(toDto(categoryService.save(toSave)));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (categoryService.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryDto toDto(Category c) {
        return new CategoryDto(c.getId(), c.getName());
    }

    private Category toEntity(CategoryDto d) {
        Category c = new Category();
        c.setName(d.name());
        return c;
    }

    @GetMapping(params = "name")
    public List<CategoryDto> listByName(@RequestParam String name) {
        return categoryService.findByName(name).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
}
