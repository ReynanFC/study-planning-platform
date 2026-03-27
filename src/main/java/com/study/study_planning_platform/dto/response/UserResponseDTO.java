package com.study.study_planning_platform.dto.response;

public record UserResponseDTO(
        Long id,
        String userName,
        String email,
        String createdAt
) {}