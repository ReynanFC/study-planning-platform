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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService")
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TaskMapper mapper;
    @Mock private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Category category;
    private Task task;
    private TaskResponseDTO taskResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        category = new Category();
        category.setId(1L);
        category.setCategoryName("Studies");

        task = new Task();
        task.setId(1L);
        task.setTitle("Study Spring");
        task.setDescription("Learn Spring Boot");
        task.setCategory(category);
        task.setUser(user);

        taskResponseDTO = new TaskResponseDTO(
                1L, "Study Spring", "Learn Spring Boot",
                null, LocalDateTime.now(), LocalDateTime.now(),
                1L, "Studies"
        );
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return task when found and owned by user")
        void shouldReturnTaskWhenFoundAndOwnedByUser() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));
            when(mapper.toResponseDTO(task)).thenReturn(taskResponseDTO);

            TaskResponseDTO result = taskService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("Study Spring");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when task not found")
        void shouldThrowWhenTaskNotFound() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Task not found");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when task belongs to another user")
        void shouldThrowWhenTaskBelongsToAnotherUser() {
            User anotherUser = new User();
            anotherUser.setId(2L);

            when(userService.getCurrentUser()).thenReturn(anotherUser);
            when(taskRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.findById(1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("createTask")
    class CreateTask {

        @Test
        @DisplayName("should create task successfully")
        void shouldCreateTaskSuccessfully() {
            TaskRequestDTO dto = new TaskRequestDTO("Study Spring", "Learn Spring Boot", 1L);

            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
            when(mapper.toEntity(dto)).thenReturn(task);
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(mapper.toResponseDTO(task)).thenReturn(taskResponseDTO);

            TaskResponseDTO result = taskService.createTask(dto);

            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("Study Spring");
            verify(taskRepository).save(task);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when category not found")
        void shouldThrowWhenCategoryNotFound() {
            TaskRequestDTO dto = new TaskRequestDTO("Study Spring", "Learn Spring Boot", 99L);

            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.createTask(dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Category not found");

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("should set user and category on task before saving")
        void shouldSetUserAndCategoryOnTaskBeforeSaving() {
            TaskRequestDTO dto = new TaskRequestDTO("Study Spring", "Learn Spring Boot", 1L);
            Task newTask = new Task();

            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
            when(mapper.toEntity(dto)).thenReturn(newTask);
            when(taskRepository.save(any(Task.class))).thenReturn(newTask);
            when(mapper.toResponseDTO(newTask)).thenReturn(taskResponseDTO);

            taskService.createTask(dto);

            assertThat(newTask.getUser()).isEqualTo(user);
        }
    }

    @Nested
    @DisplayName("updateTask")
    class UpdateTask {

        @Test
        @DisplayName("should update task title and description")
        void shouldUpdateTaskTitleAndDescription() {
            TaskRequestDTO dto = new TaskRequestDTO("Updated Title", "Updated Desc", 1L);

            when(userService.getCurrentUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));
            when(mapper.toResponseDTO(task)).thenReturn(taskResponseDTO);

            taskService.updateTask(1L, dto);

            assertThat(task.getTitle()).isEqualTo("Updated Title");
            assertThat(task.getDescription()).isEqualTo("Updated Desc");
        }

        @Test
        @DisplayName("should move task to new category when categoryId changes")
        void shouldMoveTaskToNewCategoryWhenCategoryIdChanges() {
            Category newCategory = new Category();
            newCategory.setId(2L);
            newCategory.setCategoryName("Work");

            TaskRequestDTO dto = new TaskRequestDTO("Study Spring", "Learn Spring Boot", 2L);

            when(userService.getCurrentUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));
            when(categoryRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(newCategory));
            when(mapper.toResponseDTO(task)).thenReturn(taskResponseDTO);

            taskService.updateTask(1L, dto);

            assertThat(task.getCategory()).isEqualTo(newCategory);
        }

        @Test
        @DisplayName("should not change category when categoryId is the same")
        void shouldNotChangeCategoryWhenSameId() {
            TaskRequestDTO dto = new TaskRequestDTO("Study Spring", "Learn Spring Boot", 1L);

            when(userService.getCurrentUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));
            when(mapper.toResponseDTO(task)).thenReturn(taskResponseDTO);

            taskService.updateTask(1L, dto);

            verify(categoryRepository, never()).findByIdAndUserId(any(), any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when task not found on update")
        void shouldThrowWhenTaskNotFoundOnUpdate() {
            TaskRequestDTO dto = new TaskRequestDTO("Title", "Desc", 1L);

            when(userService.getCurrentUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.updateTask(99L, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Task not found");
        }
    }

    @Nested
    @DisplayName("deleteTask")
    class DeleteTask {

        @Test
        @DisplayName("should delete task successfully")
        void shouldDeleteTaskSuccessfully() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));

            taskService.deleteTask(1L);

            verify(taskRepository).delete(task);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when task not found on delete")
        void shouldThrowWhenTaskNotFoundOnDelete() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(taskRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.deleteTask(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Task not found");

            verify(taskRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw when task belongs to another user")
        void shouldThrowWhenTaskBelongsToAnotherUser() {
            User anotherUser = new User();
            anotherUser.setId(2L);

            when(userService.getCurrentUser()).thenReturn(anotherUser);
            when(taskRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.deleteTask(1L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(taskRepository, never()).delete(any());
        }
    }
}