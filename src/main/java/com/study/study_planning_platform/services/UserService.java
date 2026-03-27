package com.study.study_planning_platform.services;

import com.study.study_planning_platform.dto.request.UserLoginRequestDTO;
import com.study.study_planning_platform.dto.request.UserRegistrationRequestDTO;
import com.study.study_planning_platform.dto.response.LoginResponseDTO;
import com.study.study_planning_platform.dto.response.UserResponseDTO;
import com.study.study_planning_platform.entities.User;
import com.study.study_planning_platform.exceptions.ResourceNotFoundException;
import com.study.study_planning_platform.mapper.UserMapper;
import com.study.study_planning_platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserService(UserRepository repository, UserMapper mapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public UserResponseDTO register(UserRegistrationRequestDTO dto) {

        logger.info("Register User with e-mail: {}", dto.email());

        if (repository.findByEmail(dto.email()).isPresent()) {
            logger.warn("Trying to register already User with email: {}", dto.email());
            throw new DataIntegrityViolationException("Email already exists");
        }

        User user = mapper.toEntity(dto);

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return  mapper.toResponseDTO(repository.save(user));
    }

    public User validateCredentials(UserLoginRequestDTO dto) {

        logger.info("Validate User with email: {}", dto.email());

        User user = repository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!bCryptPasswordEncoder.matches(dto.password(), user.getPassword())) {
            logger.warn("Trying to login with incorrect password");
            throw new BadCredentialsException("Invalid email or password");
        }

        return user;
    }

    public User getCurrentUser() {
        logger.info("Get current User");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        logger.info("Current User: {}", email);

        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in the context"));
    }

}
