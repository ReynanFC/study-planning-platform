package com.study.study_planning_platform.services;

import com.study.study_planning_platform.dto.request.CategoryRequestDTO;
import com.study.study_planning_platform.dto.response.CategoryResponseDTO;
import com.study.study_planning_platform.entities.Category;
import com.study.study_planning_platform.entities.User;
import com.study.study_planning_platform.exceptions.ResourceNotFoundException;
import com.study.study_planning_platform.mapper.CategoryMapper;
import com.study.study_planning_platform.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper mapper, UserService service) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.userService = service;
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
    public void deleteCategory(Long id) {
        User user = userService.getCurrentUser();

        logger.info("Deleting a category by user via email: {}", user.getEmail());

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> {
                    logger.warn("Category not found or access denied with id: {}", id);
                    return new ResourceNotFoundException("Category not found");
                });

        if (!category.getTasks().isEmpty()) {
            throw new DataIntegrityViolationException("It is not possible to delete a category with related tasks");
        }

        categoryRepository.delete(category);
        logger.info("Category '{}' deleted by user: {}", category.getCategoryName(), user.getEmail());
    }
}
