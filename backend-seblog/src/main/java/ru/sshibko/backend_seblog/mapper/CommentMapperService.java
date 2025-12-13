package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.response.CommentResponse;
import ru.sshibko.backend_seblog.dto.response.UserSummaryResponse;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.service.CommentVoteService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentMapperService {

    private final UserMapperService userMapper;

    private final CommentVoteService commentVoteService;

    public CommentResponse mapToResponse(Comment comment) {
        return mapToResponse(comment, Collections.emptyList());
    }

    public CommentResponse mapToResponse(Comment comment, List<CommentResponse> replies) {
        if (comment == null) {
            return null;
        }

        UserSummaryResponse authorResponse = userMapper.mapToUserSummaryResponse(comment.getAuthor());

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(authorResponse)
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                //TODO .isDeleted(comment.getIsDeleted())
                .likeCount(commentVoteService.getCommentLikeCount(comment.getId()))
                .dislikeCount(commentVoteService.getCommentDislikeCount(comment.getId()))
                .currentUserVote(commentVoteService.getCurrentUserVoteForComment(comment.getId()))
                .replies(replies)
                .build();
    }
}
