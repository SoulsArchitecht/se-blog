package ru.sshibko.backend_seblog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

@Builder
@Schema(description = "Статистика голосования")
public record VoteStats(
        @Schema(description = "Количество лайков", example = "10")
        long likesCount,

        @Schema(description = "Количество дизлайков", example = "5")
        long dislikesCount,

        @Schema(description = "Общий рейтинг (лайки - дизлайки)", example = "8")
        long totalScore,

        @Schema(description = "Голос текущего пользователя (null если не проголосовал или не авторизирован)")
        VoteType userVote
) {
    public static VoteStats of(long likes, long dislikes, VoteType userVote) {
        return VoteStats.builder()
                .likesCount(likes)
                .dislikesCount(dislikes)
                .totalScore(likes - dislikes)
                .userVote(userVote)
                .build();
    }
}
