package com.study.study_planning_platform.repository;

import com.study.study_planning_platform.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    Page<Category> findByUserId(Long userId, Pageable pageable);

    @Query(
            "SELECT DISTINCT c FROM Category c " +
            "LEFT JOIN FETCH c.tasks " +
            "WHERE c.id = :id AND c.user.id = :userId"
    )
    Optional<Category> findByIdWithTasks(@Param("id") Long id, @Param("userId") Long userId);

}
