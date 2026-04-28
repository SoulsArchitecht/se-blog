package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sshibko.backend_seblog.dto.ApiResponse;
import ru.sshibko.backend_seblog.dto.request.CommentCreateRequest;
import ru.sshibko.backend_seblog.dto.request.CommentUpdateRequest;
import ru.sshibko.backend_seblog.dto.response.CommentResponse;
import ru.sshibko.backend_seblog.exception.ErrorCode;
import ru.sshibko.backend_seblog.exception.SuccessCode;
import ru.sshibko.backend_seblog.exception.ValidationException;
import ru.sshibko.backend_seblog.service.CommentService;
import ru.sshibko.backend_seblog.service.MessageService;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment API")
public class CommentController {

    private final CommentService commentService;

    private final Locale locale = LocaleContextHolder.getLocale();

    private final MessageService messageService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @Operation(summary = "Создать новый комментарий к существующему посту",
            description = "Создает новый комментарий к существующему посту. Доступно для USER, MODERATOR, ADMIN")
    public ApiResponse<CommentResponse> createComment(@PathVariable UUID postId,
                                                      @RequestBody @Valid CommentCreateRequest request) {
        CommentResponse response = commentService.createComment(postId, request);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENT_ADDED, locale);

        return ApiResponse.success(response, message);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "Показывает все комментарии для существующего поста постранично",
            description = """
                    Показывает все комментарии для существующего указанного поста постранично.
                    Доступно для USER, MODERATOR, ADMIN и для неавтаризованных пользователей
                    """)
    public ApiResponse<List<CommentResponse>> getCommentsByPost(
            @PathVariable UUID postId,
            @ParameterObject @PageableDefault(page = 0, size = 20, sort = "createdAt",
                    direction = Sort.Direction.ASC) Pageable pageable) {
            Page<CommentResponse> comments = commentService.getCommentsByPost(postId, pageable);
            String message = messageService.getSuccessMessage(SuccessCode.COMMENTS_RECEIVED, locale);

            return ApiResponse.success(comments.getContent(), message);
    }

    @GetMapping("/tree")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Показывает дерево комментариев для существующего укоазанного поста",
            description = """
                    Показывает дерево комментариев для существующего указанного поста.
                    Доступно для USER, MODERATOR, ADMIN и для неавтаризованных пользователей
                    """)
    public ApiResponse<List<CommentResponse>> getCommentTree(@PathVariable UUID postId) {
        List<CommentResponse> comments = commentService.getCommentTree(postId);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENTS_RECEIVED, locale);

        return ApiResponse.success(comments, message);
    }

    @GetMapping("/{commentId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Возвращает комментарий по его ID",
            description = "Возвращает комментарий по его ID. Доступно для USER, MODERATOR, ADMIN" +
                    " и неавторизованных пользователей")
    public ApiResponse<CommentResponse> getCommentById(
            @PathVariable UUID postId,
            @PathVariable UUID commentId) {
        CommentResponse commentResponse = commentService.getCommentById(commentId);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENTS_RECEIVED, locale);

        if (!commentResponse.postId().equals(postId)) {
            throw new ValidationException(
                    ErrorCode.COMMENT_PARENT_MISMATCH,
                    "Comment does not belong to specified post",
                    "Comment " + commentId + " belongs to post " + commentResponse.postId() + ", not " + postId
            );
        }

        return ApiResponse.success(commentResponse, message);
    }

    @PatchMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @Operation(summary = "Обновляет существующий комментарий",
            description = """
                    Обновляет существующий комментарий по его ID и ID поста.
                    Доступно для USER, MODERATOR, ADMIN.
                    Пользователь должен быть автором комментария
                    """)
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @RequestBody @Valid CommentUpdateRequest request) {
        CommentResponse updatedComment = commentService.updateComment(postId, commentId, request);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENTS_RECEIVED, locale);

        return ApiResponse.success(updatedComment, message);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаляет существующий комментарий",
            description = """
                    Удаляет существующий комментарий по его ID и ID поста.
                    Доступно для USER, MODERATOR, ADMIN.
                    Пользователь должен быть автором комментария
                    """)
    public ApiResponse<Void> deleteComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId
    ) {
        commentService.deleteComment(postId, commentId);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENTS_RECEIVED, locale);

        return ApiResponse.success(message);
    }
}
