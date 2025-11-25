package ru.sshibko.backend_seblog.mapper;

import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.dto.UserSummaryDto;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;

@Service
public class UserMapperService {

    public UserSummaryDto toSummaryDto(User user) {
        if (user == null) {
            return null;
        }
        var profile = user.getProfile();
        return UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(profile != null ? profile.getDisplayName() : user.getUsername())
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .createAt(user.getCreatedAt())
                .build();
    }

    public UserProfileDto toProfileDto(UserProfile profile) {
        if (profile == null) return null;
        return UserProfileDto.builder()
                .id(profile.getUser().getId())
                .displayName(profile.getDisplayName())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .birthDate(profile.getBirthDate())
                .phone(profile.getPhone())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .build();
    }
}
