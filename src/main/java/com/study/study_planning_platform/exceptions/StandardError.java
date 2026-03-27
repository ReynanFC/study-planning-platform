package com.study.study_planning_platform.exceptions;

import java.time.Instant;
import java.util.Map;


public record StandardError(
        Instant timestamp,
        Integer status,
        String message,
        String path,
        Map<String, String> errors
) {
    public StandardError(Instant timestamp, Integer status, String message, String path) {
        this(timestamp, status, message, path, null);
    }
}
