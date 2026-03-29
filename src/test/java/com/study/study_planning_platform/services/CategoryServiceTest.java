package com.study.study_planning_platform.services;

import com.study.study_planning_platform.dto.request.CategoryRequestDTO;
import com.study.study_planning_platform.dto.response.CategoryResponseDTO;
import com.study.study_planning_platform.dto.response.CategoryWithTasksResponseDTO;
import com.study.study_planning_platform.entities.Category;
import com.study.study_planning_platform.entities.User;
import com.study.study_planning_platform.exceptions.ResourceNotFoundException;
import com.study.study_planning_platform.mapper.CategoryMapper;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService")
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private CategoryMapper mapper;
    @Mock private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category category;
    private CategoryResponseDTO categoryResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        category = new Category();
        category.setId(1L);
        category.setCategoryName("Studies");

        categoryResponseDTO = new CategoryResponseDTO(1L, "Studies", List.of(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("createCategory")
    class CreateCategory {

        @Test
        @DisplayName("should create category successfully")
        void shouldCreateCategorySuccessfully() {
            CategoryRequestDTO dto = new CategoryRequestDTO("Studies");

            when(userService.getCurrentUser()).thenReturn(user);
            when(mapper.toEntity(dto)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);
            when(mapper.toResponseDTO(category)).thenReturn(categoryResponseDTO);

            CategoryResponseDTO result = categoryService.createCategory(dto);

            assertThat(result).isNotNull();
            assertThat(result.categoryName()).isEqualTo("Studies");
            verify(categoryRepository).save(category);
        }

        @Test
        @DisplayName("should associate user to category before saving")
        void shouldAssociateUserToCategoryBeforeSaving() {
            CategoryRequestDTO dto = new CategoryRequestDTO("Studies");
            Category newCategory = new Category();

            when(userService.getCurrentUser()).thenReturn(user);
            when(mapper.toEntity(dto)).thenReturn(newCategory);
            when(categoryRepository.save(newCategory)).thenReturn(newCategory);
            when(mapper.toResponseDTO(newCategory)).thenReturn(categoryResponseDTO);

            categoryService.createCategory(dto);

            assertThat(newCategory.getUser()).isEqualTo(user);
        }
    }

    @Nested
    @DisplayName("getAllCategories")
    class GetAllCategories {

        @Test
        @DisplayName("should return page of categories for current user")
        void shouldReturnPageOfCategoriesForCurrentUser() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Category> categoryPage = new PageImpl<>(List.of(category));

            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByUserId(1L, pageable)).thenReturn(categoryPage);
            when(mapper.toResponseDTO(category)).thenReturn(categoryResponseDTO);

            Page<CategoryResponseDTO> result = categoryService.getAllCategories(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).categoryName()).isEqualTo("Studies");
        }

        @Test
        @DisplayName("should return empty page when user has no categories")
        void shouldReturnEmptyPageWhenNoCategories() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Category> emptyPage = new PageImpl<>(List.of());

            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByUserId(1L, pageable)).thenReturn(emptyPage);

            Page<CategoryResponseDTO> result = categoryService.getAllCategories(pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCategoryWithTasks")
    class GetCategoryWithTasks {

        @Test
        @DisplayName("should return category with tasks when found")
        void shouldReturnCategoryWithTasksWhenFound() {
            CategoryWithTasksResponseDTO withTasksDTO =
                    new CategoryWithTasksResponseDTO(1L, "Studies", List.of(), LocalDateTime.now());

            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdWithTasks(1L, 1L)).thenReturn(Optional.of(category));
            when(mapper.toResponseWithTasksDTO(category)).thenReturn(withTasksDTO);

            CategoryWithTasksResponseDTO result = categoryService.getCategoryWithTasks(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when category not found")
        void shouldThrowWhenCategoryNotFound() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdWithTasks(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.getCategoryWithTasks(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Category not found");
        }
    }

    @Nested
    @DisplayName("updateCategory")
    class UpdateCategory {

        @Test
        @DisplayName("should update category name successfully")
        void shouldUpdateCategoryNameSuccessfully() {
            CategoryRequestDTO dto = new CategoryRequestDTO("New Name");
            CategoryResponseDTO updatedDTO = new CategoryResponseDTO(1L, "New Name", List.of(), LocalDateTime.now());

            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
            when(mapper.toResponseDTO(category)).thenReturn(updatedDTO);

            CategoryResponseDTO result = categoryService.updateCategory(1L, dto);

            assertThat(category.getCategoryName()).isEqualTo("New Name");
            assertThat(result.categoryName()).isEqualTo("New Name");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when category not found on update")
        void shouldThrowWhenCategoryNotFoundOnUpdate() {
            CategoryRequestDTO dto = new CategoryRequestDTO("New Name");

            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(99L, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Category not found");
        }

        @Test
        @DisplayName("should throw when category belongs to another user")
        void shouldThrowWhenCategoryBelongsToAnotherUser() {
            CategoryRequestDTO dto = new CategoryRequestDTO("New Name");
            User anotherUser = new User();
            anotherUser.setId(2L);

            when(userService.getCurrentUser()).thenReturn(anotherUser);
            when(categoryRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(1L, dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteCategory")
    class DeleteCategory {

        @Test
        @DisplayName("should delete category successfully when no tasks")
        void shouldDeleteCategorySuccessfullyWhenNoTasks() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
            when(taskRepository.existsByCategoryId(1L)).thenReturn(false);

            categoryService.deleteCategory(1L);

            verify(categoryRepository).delete(category);
        }

        @Test
        @DisplayName("should throw DataIntegrityViolationException when category has tasks")
        void shouldThrowWhenCategoryHasTasks() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
            when(taskRepository.existsByCategoryId(1L)).thenReturn(true);

            assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessage("It is not possible to delete a category with related tasks");

            verify(categoryRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when category not found on delete")
        void shouldThrowWhenCategoryNotFoundOnDelete() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(categoryRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Category not found");

            verify(categoryRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw when category belongs to another user")
        void shouldThrowWhenCategoryBelongsToAnotherUser() {
            User anotherUser = new User();
            anotherUser.setId(2L);

            when(userService.getCurrentUser()).thenReturn(anotherUser);
            when(categoryRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(categoryRepository, never()).delete(any());
        }
    }
}