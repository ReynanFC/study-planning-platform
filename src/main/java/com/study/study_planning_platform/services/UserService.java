package com.study.study_planning_platform.services;

import com.study.study_planning_platform.repository.UserRepository;
import static com.study.study_planning_platform.mapper.UserMapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
