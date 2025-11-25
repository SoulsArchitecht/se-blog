package ru.sshibko.backend_seblog.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sshibko.backend_seblog.dto.PostDto;
import ru.sshibko.backend_seblog.dto.PostSummaryDto;
import ru.sshibko.backend_seblog.mapper.PostMapperService;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;
import ru.sshibko.backend_seblog.model.repository.PostRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;

    private final PostMapperService postMapper;

    private final ViewLogService viewLogService;

    @Transactional(readOnly = true)
    public PostDto getPostById(UUID id, UUID userId) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (userId != null && !viewLogService.hasUserViewedToday(id, userId)) {
            incrementPostViews(id);
            viewLogService.logView(id, userId);
        }

        return postMapper.toFull(post, userId);
    }

    @Transactional(readOnly = true)
    public PostSummaryDto getSummaryById(UUID id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return postMapper.toSummary(post);
    }

    public Page<PostSummaryDto> getPostSummary(String status,
                                               String typeSlug,
                                               String tagSlug,
                                               String search,
                                               int page,
                                               int size) {
        Pageable pageable = PageRequest.of(page, size);
        PostStatus statusEnum = status !=  null ? PostStatus.valueOf(status.toLowerCase()) : null;

        return postRepo.findAllFiltered(statusEnum, typeSlug, tagSlug, search, pageable)
                .map(postMapper::toSummary);
    }

    public Page<PostSummaryDto> getAllPostsPublished(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepo.findAllByStatus(PostStatus.PUBLISHED, pageable)
                .map(postMapper::toSummary);
    }

    @Transactional
    protected void incrementPostViews(UUID postId) {
        postRepo.incrementViewCount(postId);
    }

}
