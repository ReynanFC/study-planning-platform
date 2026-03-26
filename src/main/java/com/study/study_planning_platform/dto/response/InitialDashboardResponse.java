package com.study.study_planning_platform.dto.response;

public record InitialDashboardResponse(
        long totalTasks,
        long completedTasks,
        double overallPercentage
) {}