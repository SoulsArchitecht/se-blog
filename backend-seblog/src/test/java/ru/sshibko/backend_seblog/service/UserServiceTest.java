package ru.sshibko.backend_seblog.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.sshibko.backend_seblog.exception.AuthenticationException;
import ru.sshibko.backend_seblog.exception.NotFoundException;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;
import ru.sshibko.backend_seblog.model.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .username("testuser")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return user when authenticated user exists in DB")
    void getCurrentUserShouldReturnUserWhenAuthenticated() {
        //Arrange
        var authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password",
                Collections.emptyList()
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        //Act
        User result = userService.getCurrentUser();

        //Assert
        assertThat(result).isEqualTo(testUser);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw AuthenticationException when not authenticated")
    void getCurrentUserShouldThrowExceptionWhenNotAuthenticated() {
        //Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        //Act & Assert
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Пользователь не авторизован");

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when user not found in DB")
    void getCurrentUserShouldThrowExceptionWhenUserNotFoundInDb() {
        //Arrange
        var authentication = new UsernamePasswordAuthenticationToken(
                "unknown@example.com",
                "password",
                Collections.emptyList()
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with identifier '" + authentication.getName() + "' not found");
    }
}