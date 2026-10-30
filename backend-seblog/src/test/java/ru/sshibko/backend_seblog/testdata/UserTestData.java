package ru.sshibko.backend_seblog.testdata;

import ru.sshibko.backend_seblog.dto.UserDto;
import ru.sshibko.backend_seblog.dto.security.AuthRequest;
import ru.sshibko.backend_seblog.dto.security.RefreshTokenRequest;
import ru.sshibko.backend_seblog.dto.security.RegisterRequest;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;

import java.util.UUID;

public final class UserTestData {

    private UserTestData() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User createUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .passwordHash("encodedPasswordHash123")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static User crateUserWithRole(UserRole role) {
        User user = createUser();
        user.setRole(role);
        return user;
    }

    public static UserDto createUserDto(User user) {
        return UserDto.builder()
                .id(user != null ? user.getId() : UUID.randomUUID())
                .username(user != null ? user.getUsername() : "default_user")
                .email(user != null ? user.getEmail() : "default@example.com")
                .role(user != null ? user.getRole().name() : UserRole.ROLE_USER.name())
                .build();
    }

    public static AuthRequest createAuthRequest(String email, String password) {
        return new AuthRequest(email, password);
    }

    public static RegisterRequest createRegisterRequest(String username, String email, String password) {
        return new RegisterRequest(username, email, password);
    }

    public static RefreshTokenRequest createRefreshTokenRequest(String token) {
        return new RefreshTokenRequest(token);
    }

/*    public static UserDto createRandomValidUserDto() {
        return UserDto.builder()
                .id(UUID.randomUUID())
                .username("random_user_" + System.currentTimeMillis())
                .email("random_" + System.currentTimeMillis())
                .role(String.valueOf(UserRole.ROLE_USER))
                .build();
    }*/
}
