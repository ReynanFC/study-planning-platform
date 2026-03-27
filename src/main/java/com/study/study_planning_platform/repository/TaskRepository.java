package com.study.study_planning_platform.repository;

import com.study.study_planning_platform.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByCategoryIdAndUserId(Long categoryId, Long userId, Pageable pageable);
}
