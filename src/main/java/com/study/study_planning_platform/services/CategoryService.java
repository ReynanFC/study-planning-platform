package com.study.study_planning_platform.services;

import com.study.study_planning_platform.dto.request.CategoryRequestDTO;
import com.study.study_planning_platform.dto.response.CategoryResponseDTO;
import com.study.study_planning_platform.entities.Category;
import com.study.study_planning_platform.entities.User;
import com.study.study_planning_platform.exceptions.ResourceNotFoundException;
import com.study.study_planning_platform.mapper.CategoryMapper;
import com.study.study_planning_platform.repository.CategoryRepository;
import com.study.study_planning_platform.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final CategoryMapper mapper;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository, TaskRepository taskRepository, CategoryMapper mapper, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.mapper = mapper;
        this.userService = userService;
    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        User user = userService.getCurrentUser();

        Category category = mapper.toEntity(dto);
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);

        logger.info("Category '{}' (ID: {}) created for user: {}", savedCategory.getCategoryName(), savedCategory.getId(), user.getEmail());
        return mapper.toResponseDTO(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        User user = userService.getCurrentUser();
        Category category = getCategoryOrThrow(id, user);

        category.setCategoryName(dto.categoryName());

        logger.info("Category ID: {} updated to '{}' by user: {}", id, category.getCategoryName(), user.getEmail());
        return mapper.toResponseDTO(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        User user = userService.getCurrentUser();
        Category category = getCategoryOrThrow(id, user);

        if (taskRepository.existsByCategoryId(id)) {
            logger.warn("Delete failed: Category ID {} has related tasks. User: {}", id, user.getEmail());
            throw new DataIntegrityViolationException("It is not possible to delete a category with related tasks");
        }

        categoryRepository.delete(category);
        logger.info("Category '{}' (ID: {}) deleted by user: {}", category.getCategoryName(), id, user.getEmail());
    }

    private Category getCategoryOrThrow(Long id, User user) {
        return categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> {
                    logger.warn("Category access failed: ID {} not found or unauthorized for user {}", id, user.getEmail());
                    return new ResourceNotFoundException("Category not found");
                });
    }
}