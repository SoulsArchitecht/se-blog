package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.sshibko.backend_seblog.dto.ApiResponse;
import ru.sshibko.backend_seblog.dto.VoteStats;
import ru.sshibko.backend_seblog.dto.request.VoteRequest;
import ru.sshibko.backend_seblog.exception.SuccessCode;
import ru.sshibko.backend_seblog.service.CommentVoteService;
import ru.sshibko.backend_seblog.service.MessageService;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments/{commentId}/vote")
@RequiredArgsConstructor
@Tag(name = "Comment Vote", description = "API для голосования за комментарии")
public class CommentVoteController {

    private final CommentVoteService commentVoteService;

    private final MessageService messageService;

    private final Locale locale = LocaleContextHolder.getLocale();

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Проголосовать за комментарий",
                description = """
                    Голосует за комментарий (LIKE или DISLIKE).
                    Если пользователь уже голосовал с таким же типом — голос удаляется.
                    Если с другим типом — голос обновляется.
                    Автор комментария не может голосовать за свой комментарий.
                    Доступно для USER, MODERATOR, ADMIN.
                    """)
    public ApiResponse<VoteStats> voteForComment(
            @Parameter(description = "ID комментария", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID commentId,
            @Valid @RequestBody VoteRequest request) {
        commentVoteService.voteForComment(commentId, request);
        VoteStats stats = commentVoteService.getVoteStats(commentId);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENT_VOTED, locale);

        return ApiResponse.success(stats, message);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить статистику голосования за комментарий")
    public ApiResponse<VoteStats> getVoteStats(
            @Parameter(description = "ID комментария") @PathVariable UUID commentId) {
        VoteStats stats = commentVoteService.getVoteStats(commentId);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENT_VOTED, locale);
        return ApiResponse.success(stats, message);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить голос за комментарий")
    public ApiResponse<VoteStats> removeComment(
            @Parameter(description = "ID комментария") @PathVariable UUID commentId) {
        commentVoteService.removeCommentVote(commentId);
        VoteStats stats = commentVoteService.getVoteStats(commentId);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENT_VOTED, locale);
        return ApiResponse.success(stats, message);
    }
}
