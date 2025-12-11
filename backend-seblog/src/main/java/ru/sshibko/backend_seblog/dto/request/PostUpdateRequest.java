package ru.sshibko.backend_seblog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.sshibko.backend_seblog.aop.annotation.ValidSlug;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;

import java.util.Set;
import java.util.UUID;

@Builder
@Schema(description = "Запрос на обновление поста")
public record PostUpdateRequest(
        @Size(max = 200, message = "Заголовок не может превышать 200 символов")
        @Schema(description = "Новый заголовок",
                example = "Обновленный заголовок")
        String title,

        @Schema(description = "Новое содержимое",
                example = "Обновленное содержимое...")
        String content,

        @ValidSlug
        @Schema(description = "Новый slug",
                example = "new-post-slug")
        String slug,

        @Schema(description = "Новый статус",
                example = "PUBLISHED")
        PostStatus status,

        @Schema(description = "Новый тип поста",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID postTypeId,

        @Schema(description = "Новые теги",
                example = "[\"updated\", \"tags\"]")
        Set<String> tagNames
) {}
