package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sshibko.backend_seblog.dto.request.PostCreateRequest;
import ru.sshibko.backend_seblog.dto.request.PostUpdateRequest;
import ru.sshibko.backend_seblog.dto.response.PagedResponse;
import ru.sshibko.backend_seblog.dto.response.PostResponse;
import ru.sshibko.backend_seblog.service.PostService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PostController {

    private final PostService postService;

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_MODERATOR')")
    @Operation(summary = "Создать новый пост",
    description = "Создает новый пост. Доступно для USER, MODERATOR, ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@Valid @RequestBody PostCreateRequest request){
        return postService.createPost(request);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Getting post by slug",
    description = "Getting post by slug. Access for all")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostBySlug(
            @Parameter(description = "Post's slug",
            example = "windows-11-install")
            @PathVariable String slug){
        return postService.getPostBySlug(slug);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Getting post by ID", description = "Returns post by ID. Access for all")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostById(
            @Parameter(description = "post ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return postService.getPostById(id);
    }

    @GetMapping("/published")
    @Operation(summary = "Getting published posts",
            description = "Returns published posts with pagination. Access for all")
    @ResponseStatus(HttpStatus.OK)
    public PagedResponse<PostResponse> getPublishedPosts(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field for sorting by", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostResponse> posts = postService.getPublishedPosts(pageable);

        return PagedResponse.of(posts);
    }

    @GetMapping("/tag/{tagId}")
    @Operation(summary = "Get post by tag",
            description = "Returns posts by tag with pagination. Access for all")
    @ResponseStatus(HttpStatus.OK)
    public PagedResponse<PostResponse> getPostsByTag(
            @Parameter(description = "tag ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID tagId,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostResponse> posts = postService.getPostsByTag(tagId, pageable);

        return PagedResponse.of(posts);
    }

    @GetMapping("/type/{postTypeName}")
    @Operation(summary = "Get post by type name", description = "Returns post by type name. Access for all")
    @ResponseStatus(HttpStatus.OK)
    public PagedResponse<PostResponse> getPostsByTypeName(
            @Parameter(description = "post type name", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String postTypeName,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostResponse> posts = postService.getPostsByTypeName(postTypeName, pageable);

        return PagedResponse.of(posts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@postService.canEditPost(#id)")
    @Operation(summary = "Обновить пост",
            description = "Обновляет существующий пост. Доступно для автора поста, MODERATOR, ADMIN")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse updatePost(
            @Parameter(description = "ID поста", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,

            @Valid @RequestBody PostUpdateRequest request) {

        return postService.updatePost(id, request);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("@postService.canPublishPost(#id)")
    @Operation(summary = "Опубликовать пост",
            description = "Публикует черновик. Доступно для автора поста, MODERATOR, ADMIN")
    public PostResponse publishPost(
            @Parameter(description = "ID поста", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {

        return postService.publishPost(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@postService.canDeletePost(#id)")
    @Operation(summary = "Удалить пост",
            description = "Удаляет пост. Доступно для автора поста, MODERATOR, ADMIN")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "ID поста", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserId(Principal principal){
        return principal != null ? UUID.fromString(principal.getName()) : null;
    }
}
