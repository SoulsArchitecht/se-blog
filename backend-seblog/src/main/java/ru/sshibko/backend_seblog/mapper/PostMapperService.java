package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.PostDto;
import ru.sshibko.backend_seblog.dto.PostSummaryDto;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostMapperService {

    private final UserMapperService userMapper;

    private final PostTypeMapperService postTypeMapper;

    private final TagMapperService tagMapper;

    public PostDto toFull(Post post, UUID currentUserId) {
        if (post == null) {
            return null;
        }
        long upvotes = post.getVotes().stream()
                .filter(v -> v.getType() == VoteType.UP).count();
        long downvotes = post.getVotes().stream()
                .filter(v -> v.getType() == VoteType.DOWN).count();

        boolean hasUp = post.getVotes().stream()
                .anyMatch(v -> v.getUser().getId().equals(currentUserId)
                && v.getType() == VoteType.UP);
        boolean hasDown = post.getVotes().stream()
                .anyMatch(v -> v.getUser().getId().equals(currentUserId)
                && v.getType() == VoteType.DOWN);


        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .slug(post.getSlug())
                .status(post.getStatus().name())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(userMapper.toSummaryDto(post.getAuthor()))
                .type(postTypeMapper.toDto(post.getType()))
                .tags(post.getTags().stream().map(tagMapper::toDto).collect(Collectors.toSet()))
                .upvotes((int) upvotes)
                .downvotes((int) downvotes)
                .userHasUpvoted(hasUp)
                .userHasDownvoted(hasDown)
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount()) // ← добавлено
                .build();
    }

    public PostSummaryDto toSummary(Post post) {
        if (post == null) return null;
        long upvotes = post.getVotes().stream()
                .filter(v -> v.getType() == VoteType.UP).count();
        long downvotes = post.getVotes().stream()
                .filter(v -> v.getType() == VoteType.DOWN).count();

        return PostSummaryDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .author(userMapper.toSummaryDto(post.getAuthor()))
                .type(postTypeMapper.toDto(post.getType()))
                .createdAt(post.getCreatedAt())
                .commentCount(post.getComments().size())
                .upvotes((int) upvotes)
                .viewCount(post.getViewCount()) // ← добавлено
                .build();
    }

    public Set<PostSummaryDto> toSummarySet(Set<Post> posts, String currentUserId) {
        return posts.stream()
                .map(this::toSummary)
                .collect(Collectors.toSet());
    }
}
