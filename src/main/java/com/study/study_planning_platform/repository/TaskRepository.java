package com.study.study_planning_platform.repository;

import com.study.study_planning_platform.entities.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndUserId(Long id, Long userId);
    boolean existsByCategoryId(Long categoryId);
}
