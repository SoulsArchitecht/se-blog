package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.sshibko.backend_seblog.dto.ApiResponse;
import ru.sshibko.backend_seblog.dto.VoteStats;
import ru.sshibko.backend_seblog.dto.request.VoteRequest;
import ru.sshibko.backend_seblog.exception.SuccessCode;
import ru.sshibko.backend_seblog.service.MessageService;
import ru.sshibko.backend_seblog.service.PostVoteService;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/{postId}/vote")
@RequiredArgsConstructor
@Tag(name = "Post Vote", description = "API для голосования за посты")
public class PostVoteController {

    private final PostVoteService postVoteService;

    private final MessageService messageService;

    private final Locale locale = LocaleContextHolder.getLocale();

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Проголосовать за пост",
                description = """
                    Голосует за пост (LIKE или DISLIKE). 
                    Если пользователь уже голосовал с таким же типом — голос удаляется.
                    Если с другим типом — голос обновляется.
                    Автор поста не может голосовать за свой пост.
                    Доступно для USER, MODERATOR, ADMIN.
                    """)
    public ApiResponse<VoteStats> voteForPost(
            @Parameter(description = "ID поста", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID postId,
            @Valid @RequestBody VoteRequest request) {
        postVoteService.voteForPost(postId, request);
        VoteStats stats = postVoteService.getVoteStats(postId);
        String message = messageService.getSuccessMessage(SuccessCode.POST_VOTED, locale);

        return ApiResponse.success(stats, message);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить голос за пост",
                description = """
                    Удаляет голос текущего пользователя за пост.
                    Доступно для USER, MODERATOR, ADMIN.
                    """)
    public ApiResponse<VoteStats> removeVoteForPost(
            @Parameter(description = "ID поста", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID postId) {
        postVoteService.removePostVote(postId);
        VoteStats stats = postVoteService.getVoteStats(postId);
        String message = messageService.getSuccessMessage(SuccessCode.POST_VOTE_REMOVED, locale);

        return ApiResponse.success(stats, message);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить статистику голосования",
            description = """
                    Возвращает полную статистику голосования за пост: 
                    количество лайков, дизлайков, общий рейтинг и голос текущего пользователя.
                    Доступно для всех (включая неавторизованных).
                    """)
    public ApiResponse<VoteStats> getVoteStatsForPost(
            @Parameter(description = "ID поста", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID postId) {
        VoteStats stats = postVoteService.getVoteStats(postId);
        String message = messageService.getSuccessMessage(SuccessCode.POST_VOTE_STATS_RECEIVED, locale);

        return ApiResponse.success(stats, message);
    }


}
