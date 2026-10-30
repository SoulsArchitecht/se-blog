package ru.sshibko.backend_seblog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sshibko.backend_seblog.dto.UserDto;
import ru.sshibko.backend_seblog.dto.security.AuthRequest;
import ru.sshibko.backend_seblog.dto.security.AuthResponse;
import ru.sshibko.backend_seblog.dto.security.RegisterRequest;
import ru.sshibko.backend_seblog.exception.AuthenticationException;
import ru.sshibko.backend_seblog.mapper.UserMapperService;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.repository.UserRepository;
import ru.sshibko.backend_seblog.security.JwtUtils;
import ru.sshibko.backend_seblog.testdata.UserTestData;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Auth Service")
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;

    @Mock private JwtUtils jwtUtils;

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private UserMapperService userMapperService;

    @Mock private UserProfileService userProfileService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDetails testUserDetails;
    private UserDto testUserDto;
    private final String RAW_PASSWORD = "rawPassword123";
    private final String ENCODED_PASSWORD = "encodedPasswordHash";
    private final String ACCESS_TOKEN = "mock-access-token";
    private final String REFRESH_TOKEN = "mock-refresh-token";

    @BeforeEach
    void setUp() {
        testUser = UserTestData.createUser();
        testUserDto = UserTestData.createUserDto(testUser);

        //testUserDetails = mock(UserDetails.class);
    }

    @Nested
    @DisplayName("Login Method Tests")
    class LoginTests {

        @Test
        @DisplayName("Should return AtuhResponse when credentials are valid")
        void loginShouldReturnAuthResponseWhenCredentialsAreValid() {
            //Arrange
            AuthRequest request = UserTestData.createAuthRequest(testUser.getEmail(), RAW_PASSWORD);
            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
            when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn(ACCESS_TOKEN);
            when(jwtUtils.generateRefreshToken(any(UserDetails.class))).thenReturn(REFRESH_TOKEN);
            when(userMapperService.mapToUserDto(testUser)).thenReturn(testUserDto);

            //Act
            AuthResponse response = authService.login(request);

            //Assert
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(response.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
            assertThat(response.getUser()).isEqualTo(testUserDto);

            verify(authenticationManager, times(1)).authenticate(any());
            verify(userRepository, times(1)).findByEmail(request.getEmail());
        }

        @Test
        @DisplayName("Should throw AuthenticationException when credentials are invalid")
        void loginShouldThrowAuthenticationExceptionWhenCredentialsAreInvalid() {
            //Arrange
            AuthRequest request = UserTestData.createAuthRequest(testUser.getEmail(), "wrongPassword");
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

            //Act & Assert
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AuthenticationException.class)
                    //.hasMessageContaining("Неправильное имя пользователя или пароль");
                            .hasMessageContaining("Wrong password or username at login user with email: "
                                    + request.getEmail());

            verify(userRepository, never()).findByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("Register Method")
    class RegisterTests {

        @Test
        @DisplayName("Should register new user and return AuthResponse")
        void registerShouldCreateUserAndReturnAuthResponseWhenDataIsValid() {
            //Arrange
            RegisterRequest request = UserTestData.createRegisterRequest(
                    "newuser", RAW_PASSWORD, "new@example.com");
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User savedUser = invocation.getArgument(0);
                savedUser.setId(java.util.UUID.randomUUID());
                return savedUser;
            });

            when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn(ACCESS_TOKEN);
            when(jwtUtils.generateRefreshToken(any(UserDetails.class))).thenReturn(REFRESH_TOKEN);
            when(userMapperService.mapToUserDto(any(User.class))).thenAnswer(invocation ->
                    UserTestData.createUserDto(invocation.getArgument(0))
            );

            //Act
            AuthResponse response = authService.register(request);

            //Assert
            assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, times(1)).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();

            assertThat(capturedUser.getUsername()).isEqualTo("newuser");
            assertThat(capturedUser.getPasswordHash()).isEqualTo(ENCODED_PASSWORD);
            assertThat(capturedUser.getRole()).isEqualTo(UserRole.ROLE_USER);

            verify(userProfileService, times(1)).createDefaultProfile(any(User.class));
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when username is taken")
        void registerShouldThrowExceptionUsernameExists() {
            //Arrange
            RegisterRequest request = new RegisterRequest(
                    "existinguser",RAW_PASSWORD, "new@example.com");
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(testUser));

            //Act & Assert
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(ru.sshibko.backend_seblog.exception.UserAlreadyExistsException.class)
                    .hasMessageContaining("User already exists with username: " + request.getUsername());

            verify(userRepository, never()).findByEmail(anyString());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email is taken")
        void registerShouldThrownExceptionWhenEmailExists() {
            //Arrange
            RegisterRequest request = new RegisterRequest(
                    "newuser", RAW_PASSWORD, "existing@example.com");
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

            //Act & Assert
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(ru.sshibko.backend_seblog.exception.UserAlreadyExistsException.class)
                    .hasMessageContaining("User already exists with email: " + request.getEmail());

            verify(userRepository, never()).save(any());
        }
    }
}