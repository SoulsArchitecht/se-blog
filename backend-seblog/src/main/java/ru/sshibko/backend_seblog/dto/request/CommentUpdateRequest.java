package ru.sshibko.backend_seblog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Запрос на обновление комментария")
public record CommentUpdateRequest(
        @NotBlank(message = "Текст комментария обязателен")
        @Size(max = 1000, message = "Комментарий не может превышать 1000 символов")
        @Schema(description = "Новый текст комментария",
                example = "Обновленный комментарий",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String content
) {
}
