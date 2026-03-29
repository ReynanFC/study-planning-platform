package com.study.study_planning_platform.services;

import com.study.study_planning_platform.dto.request.TaskRequestDTO;
import com.study.study_planning_platform.dto.response.TaskResponseDTO;
import com.study.study_planning_platform.entities.Category;
import com.study.study_planning_platform.entities.Task;
import com.study.study_planning_platform.entities.User;
import com.study.study_planning_platform.exceptions.ResourceNotFoundException;
import com.study.study_planning_platform.mapper.TaskMapper;
import com.study.study_planning_platform.repository.CategoryRepository;
import com.study.study_planning_platform.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper mapper;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository, TaskMapper mapper, UserService userService) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.userService = userService;
    }

    public TaskResponseDTO findById(Long id) {
        User user = userService.getCurrentUser();
        Task task = getTaskOrThrow(id, user);

        logger.info("Task ID: {} fetched for user: {}", id, user.getEmail());
        return mapper.toResponseDTO(task);
    }

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        User user = userService.getCurrentUser();
        Category category = getCategoryOrThrow(dto.categoryId(), user);

        Task task = mapper.toEntity(dto);
        task.setUser(user);
        category.addTask(task);

        Task savedTask = taskRepository.save(task);
        logger.info("Task '{}' (ID: {}) created for user: {}", savedTask.getTitle(), savedTask.getId(), user.getEmail());

        return mapper.toResponseDTO(savedTask);
    }

    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        User user = userService.getCurrentUser();
        Task task = getTaskOrThrow(id, user);

        updateTaskCategory(task, dto.categoryId(), user);

        task.setTitle(dto.title());
        task.setDescription(dto.description());

        logger.info("Task ID: {} updated by user: {}", id, user.getEmail());
        return mapper.toResponseDTO(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        User user = userService.getCurrentUser();
        Task task = getTaskOrThrow(id, user);

        taskRepository.delete(task);
        logger.info("Task ID: {} deleted by user: {}", id, user.getEmail());
    }

    private Task getTaskOrThrow(Long id, User user) {
        return taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> {
                    logger.warn("Task access failed: ID {} not found/authorized for user {}", id, user.getEmail());
                    return new ResourceNotFoundException("Task not found");
                });
    }

    private Category getCategoryOrThrow(Long categoryId, User user) {
        return categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> {
                    logger.warn("Category access failed: ID {} not found/authorized for user {}", categoryId, user.getEmail());
                    return new ResourceNotFoundException("Category not found");
                });
    }

    private void updateTaskCategory(Task task, Long newCategoryId, User user) {
        if (newCategoryId == null || newCategoryId.equals(task.getCategory().getId())) {
            return;
        }

        logger.info("Moving task ID: {} to category ID: {}", task.getId(), newCategoryId);
        Category newCategory = getCategoryOrThrow(newCategoryId, user);

        task.getCategory().removeTask(task);
        newCategory.addTask(task);
    }
}