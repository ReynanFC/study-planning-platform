package com.study.study_planning_platform.repository;

import com.study.study_planning_platform.entities.Task;
import com.study.study_planning_platform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Task, Long> {

    Optional<User> findByEmail(String email);
}
