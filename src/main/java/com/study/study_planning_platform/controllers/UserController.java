package com.study.study_planning_platform.controllers;

import com.study.study_planning_platform.controllers.docs.UserControllerDocs;
import com.study.study_planning_platform.dto.response.UserResponseDTO;
import com.study.study_planning_platform.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController implements UserControllerDocs {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<UserResponseDTO> getUser() {
        logger.info("GET /user/me");
        return ResponseEntity.ok(userService.getUser());
    }
}
