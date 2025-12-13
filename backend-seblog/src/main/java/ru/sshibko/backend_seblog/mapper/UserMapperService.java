package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.response.UserResponse;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;

@Service
public class UserMapperService {

    UserResponse mapToResponse(User user){
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
    }
}
