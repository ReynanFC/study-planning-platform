package com.study.study_planning_platform.dto.response;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long categoryId,
        String categoryName
) {}
