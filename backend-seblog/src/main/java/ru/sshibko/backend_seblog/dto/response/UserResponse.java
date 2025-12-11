package ru.sshibko.backend_seblog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Ответ с рег данными пользователя")
public record UserResponse(
        @Schema(description = "ID пользователя",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "ник пользователя",
                example = "Логинов")
        String username,

        @Schema(description = "почта пользователя",
                example = "user@mail.ru")
        String email,

        @Schema(description = "имя пользователя",
                example = "Логинов")
        String firstName,

        @Schema(description = "фамилия пользователя",
                example = "Логинов")
        String lastName,

        @Schema(description = "аватар пользователя")
        String avatarUrl,

        @Schema(description = "дата регистрации пользователя",
                example = "12-20-2005")
        LocalDateTime registeredAt
) {
}
