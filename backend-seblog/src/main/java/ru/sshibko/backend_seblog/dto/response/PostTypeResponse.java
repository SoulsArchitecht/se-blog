package ru.sshibko.backend_seblog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Ответ с информацией о типе поста")
public record PostTypeResponse(
        @Schema(description = "ID типа",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Название",
                example = "Hardware")
        String name,

        @Schema(description = "Slug",
                example = "article")
        String slug,

        @Schema(description = "Иконка",
                example = "fa-file-text")
        String icon,

        @Schema(description = "Цвет",
                example = "#3498db")
        String colorHex,

        @Schema(description = "Дата создания",
                example = "2024-01-15T10:00:00")
        LocalDateTime createdAt
) implements Serializable {}
