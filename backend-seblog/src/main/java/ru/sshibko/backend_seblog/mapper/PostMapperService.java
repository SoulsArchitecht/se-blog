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
        UserSummaryResponse authorResponse = userMapper.mapToUserSummaryResponse(post.getAuthor());
        VoteType currentUserVote = postVoteService.getCurrentUserVoteForPost(post.getId());
        Integer likeCount = postVoteService.getPostLikeCount(post.getId());
        Integer dislikeCount = postVoteService.getPostDislikeCount(post.getId());

        log.info("before tagResponse");

        Set<TagResponse> tagResponses = post.getTags().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        //.slug(slugService.generateSlug(tag.getName()))
                        .createdAt(tag.getCreatedAt())
                        .postCount(1)
                        .build())
                .collect(Collectors.toSet());

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
                .commentCount(post.getComments().size())
                .comments(post.getComments().stream()
                        .map(comment -> CommentResponse.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .author(userMapper.mapToUserSummaryResponse(comment.getAuthor()))
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                //.parentId(comment.getParent().getId())
                                //.postId(comment.getPost().getId())
                                .build())
                        .collect(Collectors.toList()))
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .currentUserVote(currentUserVote)
                .build();
    }
}
