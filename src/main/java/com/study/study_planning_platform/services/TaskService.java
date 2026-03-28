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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper mapper;
    private final UserService userService;
    private Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository, TaskMapper mapper, UserService userService) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.userService = userService;
    }

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        User user = userService.getCurrentUser();

        logger.info("Creating task for user: {} in category: {}", user.getEmail(), dto.categoryId());

        Category category = categoryRepository.findByIdAndUserId(dto.categoryId(), user.getId())
                .orElseThrow(() -> {
                    logger.warn("Category ID {} not found or access denied for user {}", dto.categoryId(), user.getEmail());
                    return new ResourceNotFoundException("Category Not Found");
                });

        Task task = mapper.toEntity(dto);

        task.setUser(user);
        category.addTask(task);

        Task savedTask = taskRepository.save(task);
        logger.info("Task '{}' successfully created for user: {}", savedTask.getTitle(), user.getEmail());

        return mapper.toResponseDTO(savedTask);
    }

    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        User user = userService.getCurrentUser();

        logger.info("Updating task for user: {} in category: {}", user.getEmail(), dto.categoryId());

        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (dto.categoryId() != null) {
            logger.debug("Updating category for user: {} in category: {}", user.getEmail(), dto.categoryId());

            Category newcategory = categoryRepository.findByIdAndUserId(dto.categoryId(), user.getId())
                    .orElseThrow(() ->{
                        logger.warn("Category ID {} not found or access denied", dto.categoryId());
                        return new ResourceNotFoundException("Category not found");
                    });

            task.getCategory().removeTask(task);
            newcategory.addTask(task);
        }

        task.setTitle(dto.title());
        task.setDescription(dto.description());

        Task updatedTask = taskRepository.save(task);
        logger.info("Task '{}' updated for user: {}", updatedTask.getTitle(), user.getEmail());

        return mapper.toResponseDTO(updatedTask);
    }

    public Page<TaskResponseDTO> getTasksByCategory(Long categoryId, Pageable pageable) {
        User user = userService.getCurrentUser();

        logger.info("Getting tasks for category: {} in user: {}", categoryId, user.getId());

        categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> {
                    logger.warn("Category ID {} not found or access denied", categoryId);
                    return new ResourceNotFoundException("Category not found");
                });

        return taskRepository.findByCategoryIdAndUserId(categoryId, user.getId(), pageable)
                .map(mapper::toResponseDTO);
    }

    @Transactional
    public void deleteTask(Long id) {
        User user = userService.getCurrentUser();

        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Task Not Found"));

        taskRepository.delete(task);
    }
}
