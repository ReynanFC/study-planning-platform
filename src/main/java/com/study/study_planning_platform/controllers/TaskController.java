package com.study.study_planning_platform.controllers;

import com.study.study_planning_platform.controllers.docs.TaskControllerDocs;
import com.study.study_planning_platform.dto.request.TaskRequestDTO;
import com.study.study_planning_platform.dto.response.TaskResponseDTO;
import com.study.study_planning_platform.services.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController implements TaskControllerDocs {

    private final TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable Long id) {
        logger.info("GET /tasks/{}", id);
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody @Valid TaskRequestDTO dto) {
        logger.info("POST /tasks");
        return ResponseEntity.status(201).body(taskService.createTask(dto));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskRequestDTO dto) {
        logger.info("PUT /tasks/{}", id);
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        logger.info("DELETE /tasks/{}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}