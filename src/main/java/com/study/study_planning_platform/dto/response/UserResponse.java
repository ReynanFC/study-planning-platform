package com.study.study_planning_platform.dto.response;

public record UserResponse(
        Long id,
        String userName,
        String email
) {}