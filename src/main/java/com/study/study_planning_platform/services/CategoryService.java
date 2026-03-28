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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CategoryService(CategoryRepository categoryRepository, TaskRepository taskRepository, CategoryMapper mapper, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.mapper = mapper;
        this.userService = userService;
    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
         User user = userService.getCurrentUser();

         logger.info("Creating a category by user via email: {}", user.getEmail());

         Category category = mapper.toEntity(dto);
         category.setUser(user);

         Category savedCategory = categoryRepository.save(category);

         logger.info("Created a category '{}' by user: {}", savedCategory.getCategoryName(), user.getEmail());
         return mapper.toResponseDTO(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        User user = userService.getCurrentUser();

        logger.info("Updating a category by user via email: {}", user.getEmail());

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> {
                   logger.warn("Category not found or access denied with id: {}", id);
                   return new ResourceNotFoundException("Category not found");
                });

        category.setCategoryName(dto.categoryName());

        logger.info("Category '{}' updated by user: {}", category.getCategoryName(), user.getEmail());
        return mapper.toResponseDTO(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        User user = userService.getCurrentUser();

        logger.info("Deleting a category by user via email: {}", user.getEmail());

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> {
                    logger.warn("Category not found or access denied with id: {}", id);
                    return new ResourceNotFoundException("Category not found");
                });

        if (taskRepository.existsByCategoryId(id)) {
            throw new DataIntegrityViolationException("It is not possible to delete a category with related tasks");
        }

        categoryRepository.delete(category);
        logger.info("Category '{}' deleted by user: {}", category.getCategoryName(), user.getEmail());
    }
}
