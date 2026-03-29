package com.study.study_planning_platform.services;

import com.study.study_planning_platform.dto.request.UserRegistrationRequestDTO;
import com.study.study_planning_platform.dto.response.UserResponseDTO;
import com.study.study_planning_platform.entities.User;
import com.study.study_planning_platform.exceptions.ResourceNotFoundException;
import com.study.study_planning_platform.mapper.UserMapper;
import com.study.study_planning_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setUserName("username");
        user.setPassword("encodedPassword");

        userResponseDTO = new UserResponseDTO(1L, "username", "user@email.com", "2024-01-01");
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("should register user successfully")
        void shouldRegisterUserSuccessfully() {
            UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO("username", "user@email.com", "123456");

            when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(user);
            when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(user)).thenReturn(user);

            userService.register(dto);

            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should encode password before saving")
        void shouldEncodePasswordBeforeSaving() {
            UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO("username", "user@email.com", "123456");
            User newUser = new User();
            newUser.setPassword("123456");

            when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(newUser);
            when(bCryptPasswordEncoder.encode("123456")).thenReturn("encodedPassword");
            when(userRepository.save(newUser)).thenReturn(newUser);

            userService.register(dto);

            assertThat(newUser.getPassword()).isEqualTo("encodedPassword");
            verify(bCryptPasswordEncoder).encode("123456");
        }

        @Test
        @DisplayName("should throw DataIntegrityViolationException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO("username", "user@email.com", "123456");

            when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.register(dto))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessage("Email already exists");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getUser")
    class GetUser {

        @Test
        @DisplayName("should return DTO of current authenticated user")
        void shouldReturnDTOOfCurrentAuthenticatedUser() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("user@email.com");
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
            when(mapper.toResponseDTO(user)).thenReturn(userResponseDTO);

            UserResponseDTO result = userService.getUser();

            assertThat(result).isNotNull();
            assertThat(result.email()).isEqualTo("user@email.com");
            assertThat(result.userName()).isEqualTo("username");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void shouldThrowWhenUserNotFound() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("ghost@email.com");
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail("ghost@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUser())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found in the context");
        }
    }

    @Nested
    @DisplayName("getCurrentUser")
    class GetCurrentUser {

        @Test
        @DisplayName("should return current authenticated user")
        void shouldReturnCurrentAuthenticatedUser() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("user@email.com");
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));

            User result = userService.getCurrentUser();

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("user@email.com");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found in context")
        void shouldThrowWhenUserNotFoundInContext() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("ghost@email.com");
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail("ghost@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getCurrentUser())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found in the context");
        }
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("should return UserDetails when user found")
        void shouldReturnUserDetailsWhenUserFound() {
            when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));

            UserDetails result = userService.loadUserByUsername("user@email.com");

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("user@email.com");
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByEmail("ghost@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.loadUserByUsername("ghost@email.com"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }
}