package com.study.study_planning_platform.controllers;

import com.study.study_planning_platform.controllers.docs.AuthControllerDocs;
import com.study.study_planning_platform.dto.request.UserLoginRequestDTO;
import com.study.study_planning_platform.dto.request.UserRegistrationRequestDTO;
import com.study.study_planning_platform.dto.response.UserResponseDTO;
import com.study.study_planning_platform.entities.User;
import com.study.study_planning_platform.services.TokenService;
import com.study.study_planning_platform.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, TokenService tokenService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @Override
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegistrationRequestDTO dto) {
        logger.info("Register request for email: {}", dto.email());
        userService.register(dto);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<String> login(@RequestBody @Valid UserLoginRequestDTO dto) {
        logger.info("Login request for email: {}", dto.email());

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(tokenService.generateToken(user));
    }
}