package ru.sshibko.backend_seblog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Ответ с информацией о комментарии")
public record CommentResponse(
        @Schema(description = "ID комментария",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Текст комментария",
                example = "Отличная статья!")
        String content,

        @Schema(description = "Автор")
        UserSummaryResponse author,

        @Schema(description = "ID поста",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID postId,

        @Schema(description = "ID родительского комментария",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID parentId,

        @Schema(description = "Дата создания",
                example = "2024-01-15T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Дата обновления",
                example = "2024-01-15T10:15:00")
        LocalDateTime updatedAt,

        @Schema(description = "Удален ли комментарий",
                example = "false")
        Boolean isDeleted,

        @Schema(description = "Количество лайков",
                example = "10")
        Integer likeCount,

        @Schema(description = "Количество дизлайков",
                example = "2")
        Integer dislikeCount,

        @Schema(description = "Голос текущего пользователя",
                example = "LIKE")
        VoteType currentUserVote,

        @Schema(description = "Дочерние комментарии")
        List<CommentResponse> replies
) implements Serializable {}
