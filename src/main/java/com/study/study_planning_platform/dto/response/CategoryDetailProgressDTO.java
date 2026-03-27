package com.study.study_planning_platform.dto.response;

public record CategoryDetailProgressDTO(
        long totalTasks,
        long completedTasks,
        long pendingTasks,
        double completionPercentage
) {}
