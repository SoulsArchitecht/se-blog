package ru.sshibko.backend_seblog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Ответ с информацией о пользователе")
public record UserInfoResponse(
        @Schema(description = "ID пользователя",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,

        @Schema(description = "видимое имя пользователя",
                example = "Чингачгук - Большой Змей")
        String displayName,

        @Schema(description = "дата рождения пользователя",
                example = "12-20-2005")
        LocalDate birthDate,

        @Schema(description = "аватар пользователя")
        String avatarUrl,

        @Schema(description = "местонахождение пользователя",
                example = "Россия, Москва")
        String location,

        @Schema(description = "дата регистрации пользователя",
                example = "12-20-2005")
        LocalDateTime registeredAt
) implements Serializable {}
