package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.response.UserResponse;
import ru.sshibko.backend_seblog.model.entity.User;

@Service
public class UserMapperService {

    UserResponse mapToResponse(User user){
        if (user == null){
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getProfile().getFirstName())
                .lastName(user.getProfile().getLastName())
                .avatarUrl(user.getProfile().getAvatarUrl())
                .registeredAt(user.getCreatedAt())
                .build();
    }
}
