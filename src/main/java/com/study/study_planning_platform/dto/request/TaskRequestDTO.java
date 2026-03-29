package com.study.study_planning_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequestDTO(
        @NotBlank(message = "Title is required")
        @Size(max = 100)
        String title,

        String description,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {}
