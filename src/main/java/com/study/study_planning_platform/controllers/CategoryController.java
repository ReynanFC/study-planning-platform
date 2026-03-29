package com.study.study_planning_platform.controllers;

import com.study.study_planning_platform.controllers.docs.CategoryControllerDocs;
import com.study.study_planning_platform.dto.request.CategoryRequestDTO;
import com.study.study_planning_platform.dto.response.CategoryResponseDTO;
import com.study.study_planning_platform.dto.response.CategoryWithTasksResponseDTO;
import com.study.study_planning_platform.services.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Override
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody @Valid CategoryRequestDTO dto) {
        logger.info("POST /categories");
        return ResponseEntity.status(201).body(categoryService.createCategory(dto));
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    @GetMapping("/{id}/tasks")
    @Override
    public ResponseEntity<CategoryWithTasksResponseDTO> getCategoryWithTasks(@PathVariable Long id) {
        logger.info("GET /categories/{}/tasks", id);
        return ResponseEntity.ok(categoryService.getCategoryWithTasks(id));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequestDTO dto) {
        logger.info("PUT /categories/{}", id);
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("DELETE /categories/{}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}