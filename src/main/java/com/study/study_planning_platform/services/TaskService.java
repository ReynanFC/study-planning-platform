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
import org.springframework.data.crossstore.ChangeSetPersister;
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

        logger.info("Creating task with title: {} of the category with id: {}", dto.title(), dto.categoryId());

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> {
                    logger.warn("Category ID {} not found", dto.categoryId());
                    return new ResourceNotFoundException("Category Not Found");
                });

        User user = userService.getCurrentUser();




        Task task = mapper.toEntity(dto);

        task.setUser(user);
        category.addTask(task);

        logger.info("Task '{}' successfully created for user: {}", task .getTitle(), user.getEmail());

        return mapper.toResponseDTO(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        logger.info("Deleting task with id: {}", id);

        User user = userService.getCurrentUser();

        if ()
        taskRepository.deleteById(id);
    }


}
