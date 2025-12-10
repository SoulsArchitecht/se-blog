package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sshibko.backend_seblog.dto.PostCreateRequest;
import ru.sshibko.backend_seblog.dto.PostDto;
import ru.sshibko.backend_seblog.dto.PostResponse;
import ru.sshibko.backend_seblog.dto.PostSummaryDto;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.service.PostService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PostController {

    private final PostService postService;

/*    @GetMapping("/{id}")
    @Operation(summary = "Получить пост по ID")
    public PostDto getPost(@PathVariable UUID id, Principal principal){
        UUID userId = getUserId(principal);
        return postService.getPostById(id, userId);
    }

    @GetMapping
    @Operation(summary = "Список опубликованных постов")
    public Page<PostSummaryDto> getAllPostsPublished(
            @Parameter(description = "Номер страницы (0..N)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы (по умолчанию 10)")
            @RequestParam(defaultValue = "10") int size
    ) {
        return postService.getAllPostsPublished(page, size);
    }*/

    private UUID getUserId(Principal principal){
        return principal != null ? UUID.fromString(principal.getName()) : null;
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_MODERATOR')")
    @Operation(summary = "Создать новый пост")
    public PostResponse createPost(@Valid @RequestBody PostCreateRequest request){
        return postService.createPost(request);
    }
}
