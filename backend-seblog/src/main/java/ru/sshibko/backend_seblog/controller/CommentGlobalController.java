package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sshibko.backend_seblog.dto.ApiResponse;
import ru.sshibko.backend_seblog.dto.response.CommentResponse;
import ru.sshibko.backend_seblog.dto.response.PagedResponse;
import ru.sshibko.backend_seblog.exception.SuccessCode;
import ru.sshibko.backend_seblog.service.CommentService;
import ru.sshibko.backend_seblog.service.MessageService;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/comments") // <-- Обрати внимание на префикс
@RequiredArgsConstructor
@Tag(name = "Comment Global", description = "Глобальные операции с комментариями")
public class CommentGlobalController {

    private final CommentService commentService;
    private final MessageService messageService;
    private final Locale locale = LocaleContextHolder.getLocale();

    @GetMapping("/recent")
    @PreAuthorize("permitAll()") // <-- Явно указываем, что доступно всем
    @Operation(summary = "Получить последние комментарии",
            description = "Возвращает последние комментарии со всех постов. Публичный доступ.")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PagedResponse<CommentResponse>> getRecentComments(
            @Parameter(description = "Лимит комментариев", example = "5")
            @RequestParam(defaultValue = "5") int limit) {

        Page<CommentResponse> commentsPage = commentService.getRecentComments(limit);
        PagedResponse<CommentResponse> pagedCommentResponse = PagedResponse.of(commentsPage);

        String message = messageService.getSuccessMessage(SuccessCode.OPERATION_SUCCESSFUL, locale);
        return ApiResponse.success(pagedCommentResponse, message);
    }
}
