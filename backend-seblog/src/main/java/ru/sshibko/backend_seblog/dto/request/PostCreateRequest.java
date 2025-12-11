package ru.sshibko.backend_seblog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;

import java.util.Set;
import java.util.UUID;

@Builder
@Schema(description = "Запрос на создание поста")
public record PostCreateRequest(
        @NotBlank(message = "Заголовок обязателен")
        @Size(max = 200, message = "Заголовок не может превышать 200 символов")
        @Schema(description = "Заголовок поста",
                example = "Как установить Windows 11",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @NotBlank(message = "Содержимое обязательно")
        @Schema(description = "Содержимое поста",
                example = "Полное руководство по установке...",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String content,

        @NotNull(message = "Тип поста обязателен")
        @Schema(description = "ID типа поста",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        UUID postTypeId,

        @Schema(description = "Статус поста",
                example = "DRAFT",
                defaultValue = "DRAFT")
        PostStatus status,

        @Schema(description = "Список тегов", example = "[\"windows\", \"tutorial\"]")
        Set<@NotBlank String> tagNames,

        @Schema(description = "Кастомный slug", example = "windows-11-install")
        String customSlug
) {
    public PostCreateRequest {
        if (status == null) {
            status = PostStatus.DRAFT;
        }
        if (tagNames == null) {
            tagNames = Set.of();
        }
    }
}
