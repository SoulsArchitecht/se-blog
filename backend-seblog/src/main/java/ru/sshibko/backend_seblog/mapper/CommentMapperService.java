package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.CommentDto;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentMapperService {

    private final UserMapperService userMapper;

    public CommentDto toDtoWithReplies(Comment comment, String currentUserId,
                                       int depth, int maxDepth) {
        if (comment == null) {
            return null;
        }

        Set<CommentDto> replies = comment.getReplies().stream()
                .filter(c -> "PUBLISHED".equals(c.getStatus().toString()))
                .map(reply -> toDtoWithReplies(reply, currentUserId,
                        depth + 1, maxDepth))
                .filter(dto -> dto != null)
                .collect(Collectors.toSet());

        boolean hasUp = hasUserVoted(comment, currentUserId, true);
        boolean hasDown = hasUserVoted(comment, currentUserId, false);

        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .status(comment.getStatus().toString())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .author(userMapper.toSummaryDto(comment.getAuthor()))
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(replies)
                .upvotes((int) comment.getVotes().stream().filter(v -> v.getType() == VoteType.UP).count())
                .downvotes((int) comment.getVotes().stream().filter(v -> v.getType() == VoteType.DOWN).count())
                .userHasUpvoted(hasUp)
                .userHasDownvoted(hasDown)
                .build();
    }

    private boolean hasUserVoted(Comment comment, String userId, boolean isUp) {
        return comment.getVotes().stream()
                .anyMatch(vote -> vote.getUser().getId().toString().equals(userId)
                && vote.getType() == (isUp ? VoteType.UP : VoteType.DOWN));
    }
}
