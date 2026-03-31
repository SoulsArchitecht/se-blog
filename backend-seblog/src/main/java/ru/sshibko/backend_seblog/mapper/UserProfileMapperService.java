package ru.sshibko.backend_seblog.mapper;

import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.model.entity.UserProfile;

@Service
public class UserProfileMapperService {

    public UserProfile mapToEntity(UserProfileDto userProfileDto) {
        return UserProfile.builder()
                .id(userProfileDto.getId())
                .displayName(userProfileDto.getDisplayName())
                .firstName(userProfileDto.getFirstName())
                .lastName(userProfileDto.getLastName())
                .birthDate(userProfileDto.getBirthDate())
                .phone(userProfileDto.getPhone())
                .avatarUrl(userProfileDto.getAvatarUrl())
                .bio(userProfileDto.getBio())
                .location(userProfileDto.getLocation())
                .rating(userProfileDto.getRating())
                .build();
    }

    public UserProfileDto mapToDto(UserProfile userProfile) {
        return UserProfileDto.builder()
                .id(userProfile.getId())
                .displayName(userProfile.getDisplayName())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .birthDate(userProfile.getBirthDate())
                .phone(userProfile.getPhone())
                .avatarUrl(userProfile.getAvatarUrl())
                .bio(userProfile.getBio())
                .location(userProfile.getLocation())
                .rating(userProfile.getRating())
                .build();
    }
}
