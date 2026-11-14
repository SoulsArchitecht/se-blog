package ru.sshibko.backend_seblog.testdata;

import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;

import java.util.UUID;

public final class UserProfileTestData {

    public UserProfileTestData() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User createUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("author")
                .email("author@example.com")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static UserProfile createProfile(User user) {
        return UserProfile.builder()
                .id(UUID.randomUUID())
                .user(user)
                .displayName("John Lennon")
                .bio("Musician")
                .location("New-York")
                .build();
    }

    public static UserProfileDto createUpdateUserProfileDto(String bio, String location) {
        return UserProfileDto.builder()
                .bio(bio)
                .location(location)
                .build();
    }

}
