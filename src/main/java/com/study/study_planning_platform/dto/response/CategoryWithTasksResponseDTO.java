package com.study.study_planning_platform.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryWithTasksResponseDTO(
        Long id,
        String categoryName,
        List<TaskMinResponseDTO> tasks,
        LocalDateTime createdAt
) {}
