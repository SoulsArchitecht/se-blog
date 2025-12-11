package ru.sshibko.backend_seblog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Запрос на создание комментария")
public record CommentCreateRequest(
        @NotBlank(message = "Текст комментария обязателен")
        @Size(max = 1000, message = "Комментарий не может превышать 1000 символов")
        @Schema(description = "Текст комментария",
                example = "Отличная статья!",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String content,

        @Schema(description = "ID родительского комментария",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID parentId
) {
}
