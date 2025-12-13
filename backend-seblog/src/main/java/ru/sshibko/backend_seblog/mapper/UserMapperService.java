package ru.sshibko.backend_seblog.mapper;

import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.response.UserResponse;
import ru.sshibko.backend_seblog.dto.response.UserSummaryResponse;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;

import java.time.LocalDate;

@Service
public class UserMapperService {

    public UserSummaryResponse mapToUserSummaryResponse(User user) {
        if (user == null) {
            return null;
        }

        UserProfile profile = user.getProfile();
        String displayName = profile != null && profile.getDisplayName() != null
                ? profile.getDisplayName()
                : "-";

        String avatarUrl = profile != null && profile.getAvatarUrl() != null
                ? profile.getAvatarUrl()
                : "-";

        Integer registrationYear = user.getCreatedAt() != null
                ? user.getCreatedAt().getYear()
                : LocalDate.now().getYear();
        return new UserSummaryResponse(
                user.getId(),
                displayName,
                user.getUsername(),
                avatarUrl,
                registrationYear
        );
    }

/*    UserResponse mapToResponse(User user){
        if (user == null){
            return null;
        }

        String firstName = user.getProfile().getFirstName();
        if (user.getProfile().getFirstName() == null){
            firstName = "-";
        }
        String lastName = user.getProfile().getLastName();
        if (user.getProfile().getLastName() == null){
            lastName = "-";
        }
        String avatar = user.getProfile().getAvatarUrl();
        if (avatar == null){
            avatar = "-";
        }


        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                //.firstName(user.getProfile().getFirstName())
                //.lastName(user.getProfile().getLastName())
                //.avatarUrl(user.getProfile().getAvatarUrl())
                .firstName(firstName)
                .lastName(lastName)
                .avatarUrl(avatar)
                .registeredAt(user.getCreatedAt())
                .build();
    }*/
}
