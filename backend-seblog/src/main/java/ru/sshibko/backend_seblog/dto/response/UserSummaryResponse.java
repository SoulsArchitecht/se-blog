package ru.sshibko.backend_seblog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

@Schema(description = "Public user info")
public record UserSummaryResponse(
        @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Displayed name", example = "dev_serge")
        String displayName,

        @Schema(description = "Nickname", example = "serge")
        String username,

        @Schema(description = "Avatar", example = "/avatars/serge.jpg")
        String avatarUrl,

        @Schema(description = "Registration date (simple year)", example = "2025")
        Integer registrationYear
) implements Serializable {}
