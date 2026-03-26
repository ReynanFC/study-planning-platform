package com.study.study_planning_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(
        @NotBlank(message = "Category name is required")
        @Size(max = 30)
        String categoryName,

        @NotNull(message = "User ID is required")
        Long userId
) {}
