package com.study.study_planning_platform.controllers.docs;

import com.study.study_planning_platform.dto.request.CategoryRequestDTO;
import com.study.study_planning_platform.dto.response.CategoryResponseDTO;
import com.study.study_planning_platform.dto.response.CategoryWithTasksResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Categories", description = "Endpoints for managing categories")
public interface CategoryControllerDocs {

    @Operation(
            summary = "Create a category",
            description = "Creates a new category for the authenticated user",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO dto);

    @Operation(
            summary = "Find all categories",
            description = "Returns a paginated list of categories for the authenticated user",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class))
                            )
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Page<CategoryResponseDTO>> getAllCategories(int page, int size, String sortBy);

    @Operation(
            summary = "Find category with tasks",
            description = "Returns a specific category with all its tasks",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CategoryWithTasksResponseDTO.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<CategoryWithTasksResponseDTO> getCategoryWithTasks(Long id);

    @Operation(
            summary = "Update a category",
            description = "Updates the name of an existing category",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<CategoryResponseDTO> updateCategory(Long id, @RequestBody CategoryRequestDTO dto);

    @Operation(
            summary = "Delete a category",
            description = "Deletes a category. Fails if the category has related tasks",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> deleteCategory(Long id);
}