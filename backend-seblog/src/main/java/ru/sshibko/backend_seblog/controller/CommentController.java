package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.media.Schema;
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
import ru.sshibko.backend_seblog.dto.response.CommentResponse;
import ru.sshibko.backend_seblog.exception.SuccessCode;
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
    public ApiResponse<CommentResponse> createComment(@PathVariable UUID postId,
                                                      @RequestBody @Valid CommentCreateRequest request) {
        CommentResponse response = commentService.createComment(postId, request);
        String message = messageService.getSuccessMessage(SuccessCode.COMMENT_ADDED, locale);

        return ApiResponse.success(response, message);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ApiResponse<List<CommentResponse>> getCommentsByPost(
            @PathVariable UUID postId,
            @ParameterObject @PageableDefault(page = 0, size = 20, sort = "createdAt",
                    direction = Sort.Direction.ASC) Pageable pageable) {
            Page<CommentResponse> comments = commentService.getCommentsByPost(postId, pageable);
            String message = messageService.getSuccessMessage(SuccessCode.COMMENTS_RECEIVED, locale);

            return ApiResponse.success(comments.getContent(), message);
    }
}
