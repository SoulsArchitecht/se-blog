package ru.sshibko.backend_seblog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Schema(description = "Ответ с информацией о посте")
public record PostResponse(
        @Schema(description = "ID поста",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Заголовок",
                example = "Как установить Windows 11")
        String title,

        @Schema(description = "Содержимое",
                example = "Полное руководство...")
        String content,

        @Schema(description = "Slug",
                example = "windows-11-install")
        String slug,

        @Schema(description = "Статус",
                example = "PUBLISHED")
        PostStatus status,

        @Schema(description = "Дата публикации",
                example = "2024-01-15T10:30:00")
        LocalDateTime publishedAt,

        @Schema(description = "Дата создания",
                example = "2024-01-15T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Дата обновления",
                example = "2024-01-15T10:15:00")
        LocalDateTime updatedAt,

        @Schema(description = "Количество просмотров",
                example = "150")
        Long viewCount,

        @Schema(description = "Автор")
        UserSummaryResponse author,

        @Schema(description = "Тип поста")
        PostTypeResponse type,

        @Schema(description = "Теги")
        Set<TagResponse> tags,

        //TODO add Entity field
        @Schema(description = "Количество комментариев",
                example = "12")
        Integer commentCount,

        @Schema(description = "Список комментариев")
        List<CommentResponse> comments,

        @Schema(description = "Количество лайков",
                example = "45")
        Integer likeCount,

        @Schema(description = "Количество дизлайков",
                example = "3")
        Integer dislikeCount,

        @Schema(description = "Голос текущего пользователя",
                example = "LIKE")
        VoteType currentUserVote
) implements Serializable {}