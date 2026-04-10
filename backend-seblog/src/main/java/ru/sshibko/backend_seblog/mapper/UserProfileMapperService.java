package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.UserDto;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.exception.ErrorCode;
import ru.sshibko.backend_seblog.exception.NotFoundException;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileMapperService {

    private final UserRepository userRepository;

    public UserProfile mapToEntity(UserProfileDto userProfileDto) {
/*        User user = User.builder()
                .id(userProfileDto.getId())
                .username(userProfileDto.getUser().getUsername())
                .email(userProfileDto.getUser().getEmail())
                .role(UserRole.valueOf(userProfileDto.getUser().getRole()))
                .build();*/

        User user = userRepository.findById(userProfileDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "User",
                        userProfileDto.getId(),
                        "User not found with id: " + userProfileDto.getId()));

        return UserProfile.builder()
                .id(userProfileDto.getId())
                .user(user)
                .displayName(userProfileDto.getDisplayName())
                .firstName(userProfileDto.getFirstName())
                .lastName(userProfileDto.getLastName())
                .birthDate(userProfileDto.getBirthDate())
                .phone(userProfileDto.getPhone())
                .avatarUrl(userProfileDto.getAvatarUrl())
                .bio(userProfileDto.getBio())
                .location(userProfileDto.getLocation())
                .updatedAt(userProfileDto.getUpdateAt())
                .lastLoginAt(userProfileDto.getLastLoginAt())
                .rating(userProfileDto.getRating())
                .optionalEmail(userProfileDto.getOptionalEmail())
                .build();
    }

    public UserProfileDto mapToDto(UserProfile userProfile) {

        User user = userProfile.getUser();
        if (user == null) {
            throw new IllegalStateException("Profile #" + userProfile.getId() + " не связан с юзер. Проверить" +
                    "создание профиля");
        }

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build();

/*        UserDto userDto = userRepository.findById(userProfile.getId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "User",
                        userProfile.getId(),
                        "User not found with email: " + userProfile.getId()));*/

        return UserProfileDto.builder()
                .id(userProfile.getId())
                .user(userDto)
                .displayName(userProfile.getDisplayName())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .birthDate(userProfile.getBirthDate())
                .phone(userProfile.getPhone())
                .avatarUrl(userProfile.getAvatarUrl())
                .bio(userProfile.getBio())
                .location(userProfile.getLocation())
                .updateAt(userProfile.getUpdatedAt())
                .lastLoginAt(userProfile.getLastLoginAt())
                .rating(userProfile.getRating())
                .optionalEmail(userProfile.getOptionalEmail())
                .build();
    }
}
