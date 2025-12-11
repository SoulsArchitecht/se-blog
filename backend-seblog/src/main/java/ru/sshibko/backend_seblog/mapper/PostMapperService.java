package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.response.PostResponse;
import ru.sshibko.backend_seblog.dto.response.PostTypeResponse;
import ru.sshibko.backend_seblog.dto.response.TagResponse;
import ru.sshibko.backend_seblog.dto.response.UserResponse;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;
import ru.sshibko.backend_seblog.service.PostTypeService;
import ru.sshibko.backend_seblog.service.PostVoteService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostMapperService {

    private final UserMapperService userMapper;

    private final PostTypeService postTypeService;
    private final PostVoteService postVoteService;

    public PostResponse mapToResponse(Post post) {
        if (post == null) {
            return null;
        }

        PostTypeResponse typeResponse = postTypeService.getPostTypeById(post.getType().getId());
        UserResponse authorResponse = userMapper.mapToResponse(post.getAuthor());

        Set<TagResponse> tagResponses = post.getTags().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        //.slug(slugService.generateSlug(tag.getName()))
                        .createdAt(tag.getCreatedAt())
                        .postCount(tag.getPosts().size())
                        .build())
                .collect(Collectors.toSet());
        
        VoteType currentUserVote = postVoteService.getCurrentUserVoteForPost(post.getId());
        Integer likeCount = postVoteService.getPostLikeCount(post.getId());
        Integer dislikeCount = postVoteService.getPostDislikeCount(post.getId());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .slug(post.getSlug())
                .status(post.getStatus())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .viewCount(post.getViewCount())
                .author(authorResponse)
                .type(typeResponse)
                .tags(tagResponses)
                .commentCount(post.getComments().size())
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .currentUserVote(currentUserVote)
                .build();
    }
}
