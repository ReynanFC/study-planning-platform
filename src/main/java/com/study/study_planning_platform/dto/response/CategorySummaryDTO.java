package com.study.study_planning_platform.dto.response;

public record CategorySummaryDTO(
        String categoryName,
        long taskCount
) {}
