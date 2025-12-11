package ru.sshibko.backend_seblog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Ответ с информацией о пользователе")
public record TagResponse(
        @Schema(description = "ID тега",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Название",
                example = "Программирование")
        String name,

/*        @Schema(description = "Slug",
                example = "programmirovanie")
        String slug,*/

        @Schema(description = "Дата создания",
                example = "2024-01-15T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Количество постов",
                example = "42")
        Integer postCount
) {
}