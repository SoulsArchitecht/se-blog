package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.response.*;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;
import ru.sshibko.backend_seblog.service.PostTypeService;
import ru.sshibko.backend_seblog.service.PostVoteService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMapperService {

    private final UserMapperService userMapper;

    private final PostTypeService postTypeService;
    private final PostVoteService postVoteService;

    public PostResponse mapToResponse(Post post) {

        log.info("enter to mapper");
        if (post == null) {
            return null;
        }

        log.info("post not null checked");

        PostTypeResponse typeResponse = postTypeService.getPostTypeById(post.getType().getId());

        log.info("before userresponse");
        UserSummaryResponse authorResponse = userMapper.mapToUserSummaryResponse(post.getAuthor());

        Integer postCount = 1;

        log.info("before tagResponse");

        Set<TagResponse> tagResponses = post.getTags().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        //.slug(slugService.generateSlug(tag.getName()))
                        .createdAt(tag.getCreatedAt())
                        .postCount(postCount)
                        .build())
                .collect(Collectors.toSet());

        log.info("tags not null checked");
        
        VoteType currentUserVote = postVoteService.getCurrentUserVoteForPost(post.getId());
        Integer likeCount = postVoteService.getPostLikeCount(post.getId());
        Integer dislikeCount = postVoteService.getPostDislikeCount(post.getId());

        log.info("Starting to map post response");

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
                //TODO add entity field
                //.commentCount(post.getComments().size())
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .currentUserVote(currentUserVote)
                .build();
    }
}
