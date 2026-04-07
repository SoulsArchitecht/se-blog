package ru.sshibko.backend_seblog.mapper;

import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.UserDto;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;

@Service
public class UserProfileMapperService {

    public UserProfile mapToEntity(UserProfileDto userProfileDto) {
        User user = User.builder()
                .id(userProfileDto.getId())
                .username(userProfileDto.getUser().getUsername())
                .email(userProfileDto.getUser().getEmail())
                .role(UserRole.valueOf(userProfileDto.getUser().getRole()))
                .build();

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
        UserDto userDto = UserDto.builder()
                .id(userProfile.getId())
                .username(userProfile.getUser().getUsername())
                .email(userProfile.getUser().getEmail())
                .role(String.valueOf(userProfile.getUser().getRole()))
                .build();

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
