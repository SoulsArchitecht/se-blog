package ru.sshibko.backend_seblog.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sshibko.backend_seblog.dto.PostCreateRequest;
import ru.sshibko.backend_seblog.dto.PostDto;
import ru.sshibko.backend_seblog.dto.PostResponse;
import ru.sshibko.backend_seblog.dto.PostSummaryDto;
import ru.sshibko.backend_seblog.exception.ResourceNotFoundException;
import ru.sshibko.backend_seblog.mapper.PostMapperService;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.PostType;
import ru.sshibko.backend_seblog.model.entity.Tag;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;
import ru.sshibko.backend_seblog.model.repository.PostRepository;
import ru.sshibko.backend_seblog.model.repository.PostTypeRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepo;

    private final PostMapperService postMapper;

    private final ViewLogService viewLogService;

    private final PostTypeRepository postTypeRepo;

    private final TagService tagService;

    private final UserService userService;

    private final SlugService slugService;

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        PostType postType = postTypeRepo.findById(request.postTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("PostType not found"));
        String slug = slugService.generateUniqueSlug(
                request.title(),
                postRepo::existsBySlug
        );

        User currentUser = userService.getCurrentUser();

        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .slug(slug)
                .status(request.status())
                .author(currentUser)
                .type(postType)
                .viewCount(0L)
                .build();

        if (!request.tagNames().isEmpty()) {
            var tags = tagService.getOrCreateTags(request.tagNames());
            post.setTags(tags);
        }

        if (post.getStatus() == PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        Post newPost = postRepo.save(post);
        log.info("Created post {} with id {} , slug: {}",
                newPost.getTitle(), newPost.getId(), newPost.getSlug());

        return postMapper.toResponse(newPost);
    }


}
