package com.study.study_planning_platform.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryResponseDTO(
        Long id,
        String categoryName,
        List<TaskMinDTO> tasks,
        LocalDateTime createdAt
) {}
