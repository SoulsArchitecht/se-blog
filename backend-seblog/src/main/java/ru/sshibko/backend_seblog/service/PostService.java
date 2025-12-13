package ru.sshibko.backend_seblog.service;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sshibko.backend_seblog.dto.request.PostCreateRequest;
import ru.sshibko.backend_seblog.dto.request.PostUpdateRequest;
import ru.sshibko.backend_seblog.dto.response.PostResponse;
import ru.sshibko.backend_seblog.exception.ResourceNotFoundException;
import ru.sshibko.backend_seblog.exception.ValidationException;
import ru.sshibko.backend_seblog.mapper.PostMapperService;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.PostType;
import ru.sshibko.backend_seblog.model.entity.Tag;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.repository.PostRepository;
import ru.sshibko.backend_seblog.model.repository.PostTypeRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;

    private final PostMapperService postMapper;

    private final ViewLogService viewLogService;

    private final PostTypeRepository postTypeRepository;

    private final TagService tagService;

    private final UserService userService;

    private final SlugService slugService;

    private final CommentService commentService;

    private final PostTypeService postTypeService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @CacheEvict(value = {"posts", "postByType", "postByTag", "postsByAuthor"}, allEntries = true)
    public PostResponse createPost(PostCreateRequest request) {
        log.info("New post creating: {} ", request);

        User currentUser = userService.getCurrentUser();

        if (!postTypeService.existsById(request.postTypeId())) {
            throw new ValidationException("Post type not found " + request.postTypeId());
        }

        log.info("Checking slug");

/*        String slug = slugService.generateUniqueSlug(request.title(), postRepository::existsBySlug);*/

        String slug;
        if (request.customSlug() != null && !request.customSlug().isBlank()) {
            slug = request.customSlug().trim();
            if (!slugService.isValidSlug(slug)) {
                throw new ValidationException("Custom slug " + slug + " is invalid");
            }
            if (postRepository.existsBySlug(slug)) {
                throw new ValidationException("Post already exists with Slug " + slug);
            }
        } else {
            slug = slugService.generateUniqueSlug(
                    request.title(),
                    postRepository::existsBySlug
            );
        }

        log.info("finding post with slug: {}", slug);

       PostType postType = postTypeRepository.findById(request.postTypeId())
               .orElseThrow(() -> new ResourceNotFoundException(
                       "Post type not found " + request.postTypeId()));

        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .slug(slug)
                .status(request.status())
                .author(currentUser)
                .type(postType) //TODO resolve id or name
                .viewCount(0L)
                .build();

        if (!request.tagNames().isEmpty()) {
            Set<Tag> tags = tagService.getOrCreateTags(request.tagNames());
            post.setTags(tags);
        }

        if (post.getStatus() == PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        Post newPost = postRepository.save(post);
        log.info("New post created: {} (slug: {}) ", newPost.getTitle(), post.getSlug());

        return postMapper.mapToResponse(newPost);
    }

    @PreAuthorize("@postService.canEditPost(#postId)")
    @CacheEvict(value = {"posts", "postsByType", "postsByTag", "postsByAuthor"}, key = "#postId")
    public PostResponse updatePost(UUID postId, PostUpdateRequest request) {
        log.info("Обновление поста: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Пост не найден: " + postId));

        if (request.title() != null && !request.title().isBlank()) {
            post.setTitle(request.title());
        }

        if (request.content() != null) {
            post.setContent(request.content());
        }

        if (request.slug() != null && !request.slug().isBlank()) {
            String newSlug = request.slug().trim();

            if (!newSlug.equals(post.getSlug())) {
                if (!slugService.isValidSlug(newSlug)) {
                    throw new ValidationException("Incorrect format of slug: " + newSlug);
                }

                if (postRepository.existsBySlugAndIdNot(newSlug, postId)) {
                    throw new ValidationException("Slug '" + newSlug + "' already exists");
                }

                post.setSlug(newSlug);
            }
        }

        if (request.status() != null && request.status() != post.getStatus()) {
            post.setStatus(request.status());

            if (request.status() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
        }

        PostType postType = postTypeRepository.findById(request.postTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post type not found " + request.postTypeId()));

        post.setType(postType);

        if (request.tagNames() != null) {
            Set<Tag> tags = tagService.getOrCreateTags(request.tagNames());
            post.setTags(tags);
        }

        Post updatedPost = postRepository.save(post);
        log.info("Post updated: {} (ID: {})", post.getTitle(), post.getId());

        return postMapper.mapToResponse(updatedPost);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "posts", key = "#slug")
    public PostResponse getPostBySlug(String slug) {
        log.debug("Getting post by slug: {}", slug);

        Post post = postRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + slug));

        postRepository.incrementViewCount(post.getId());
        post.incrementViewCount();

        return postMapper.mapToResponse(post);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "posts", key = "#id")
    public PostResponse getPostById(UUID id) {
        log.debug("Getting post by id: {}", id);

        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return postMapper.mapToResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPublishedPosts(Pageable pageable) {
        log.debug("Getting published posts: page {}, size {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return postRepository.findAllByStatus(PostStatus.PUBLISHED, pageable)
                .map(postMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "postsByTag", key = "#tagId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PostResponse> getPostsByTag(UUID tagId, Pageable pageable) {
        log.debug("Getting posts by tag {}: page {}, size {}",
                tagId, pageable.getPageNumber(), pageable.getPageSize());

        return postRepository.findAllByTagId(tagId, pageable)
                .map(postMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "postsByType", key = "#type + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PostResponse> getPostsByType(PostType type, Pageable pageable) {
        log.debug("Getting post by type {}: page {}, size {}",
                type, pageable.getPageNumber(), pageable.getPageSize());

        return postRepository.findAllByType(String.valueOf(type), pageable)
                .map(postMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "postsByAuthor", key = "#authorId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PostResponse> getPostsByAuthor(UUID authorId, Pageable pageable) {
        log.debug("Getting post by author {}: page {}, size {}",
                authorId, pageable.getPageNumber(), pageable.getPageSize());

        return postRepository.findAllByAuthorId(authorId, pageable)
                .map(postMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> searchPosts(String query, Pageable pageable) {
        log.debug("Search post for query '{}': page {}, size {}",
                query, pageable.getPageNumber(), pageable.getPageSize());

        return postRepository.search(query, pageable)
                .map(postMapper::mapToResponse);
    }

    @PreAuthorize("@postService.canDeletePost(#postId)")
    @CacheEvict(value = {"posts", "postsByType", "postsByTag", "postsByAuthor"}, allEntries = true)
    public void deletePost(UUID postId) {
        log.info("Deleting post: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        postRepository.delete(post);
        log.info("Post deleted: {} (ID: {})", post.getTitle(), post.getId());
    }

    //Draft publication
    @PreAuthorize("@postService.canPublishPost(#postId)")
    @CacheEvict(value = {"posts", "postsByType", "postsByTag", "postsByAuthor"}, key = "#postId")
    public PostResponse publishPost(UUID postId) {
        log.info("Post publishing: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        post.setStatus(PostStatus.PUBLISHED);
        if (post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }

        post = postRepository.save(post);
        log.info("Post published: {} (ID: {})", post.getTitle(), post.getId());

        return postMapper.mapToResponse(post);
    }

    public boolean canEditPost(UUID postId) {
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (currentUser.hasRole(UserRole.ROLE_ADMIN) || currentUser.hasRole(UserRole.ROLE_MODERATOR)) {
            return true;
        }

        return post.getAuthor().getId().equals(currentUser.getId());
    }

    public boolean canDeletePost(UUID postId) {
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Пост не найден"));

        if (currentUser.hasRole(UserRole.ROLE_ADMIN)) {
            return true;
        }

        if (currentUser.hasRole(UserRole.ROLE_MODERATOR)) {
            return true;
        }

        return post.getAuthor().getId().equals(currentUser.getId());
    }

    public boolean canPublishPost(UUID postId) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.hasRole(UserRole.ROLE_ADMIN) || currentUser.hasRole(UserRole.ROLE_MODERATOR)) {
            return true;
        }

        //TODO resolve to delete user rights to publish post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return post.getAuthor().getId().equals(currentUser.getId());
    }
}
