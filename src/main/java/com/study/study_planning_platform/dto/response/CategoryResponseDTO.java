package com.study.study_planning_platform.dto.response;

import java.time.LocalDateTime;

public record CategoryResponseDTO(
        Long id,
        String categoryName,
        LocalDateTime createdAt
) {}
