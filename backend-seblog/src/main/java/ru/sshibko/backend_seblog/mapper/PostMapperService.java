package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.PostResponse;
import ru.sshibko.backend_seblog.dto.PostUpdateRequest;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.PostType;
import ru.sshibko.backend_seblog.model.repository.PostTypeRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostMapperService {

    private final  PostTypeMapperService postTypeMapper;

    private final TagMapperService tagMapper;

    public PostResponse toResponse(Post post) {
        if (post == null) {
            return null;
        }

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getSlug(),
                post.getStatus(),
                post.getPublishedAt(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViewCount(),
                post.getAuthor().getId(),
                post.getType(),
                post.getTags().stream()
                        .map(tagMapper::mapToResponse)
                        .collect(Collectors.toSet()),
                0, // TODO: Добавить логику комментариев
                0  // TODO: Добавить логику голосования
        );
    }

    public void updateEntityFromRequest(Post post, PostUpdateRequest request) {
        if (request.title() != null && !request.title().isBlank()) {
            post.setTitle(request.title());
        }

        if (request.content() != null) {
            post.setContent(request.content());
        }

        if (request.slug() != null && !request.slug().isBlank()) {
            post.setSlug(request.slug());
        }

        if (request.status() != null) {
            post.setStatus(request.status());
        }
    }
}
