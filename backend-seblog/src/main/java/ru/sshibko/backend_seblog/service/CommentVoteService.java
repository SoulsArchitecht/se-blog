package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sshibko.backend_seblog.dto.VoteStats;
import ru.sshibko.backend_seblog.dto.request.VoteRequest;
import ru.sshibko.backend_seblog.exception.ErrorCode;
import ru.sshibko.backend_seblog.exception.NotFoundException;
import ru.sshibko.backend_seblog.exception.ResourceNotFoundException;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.model.entity.CommentVote;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;
import ru.sshibko.backend_seblog.model.repository.CommentRepository;
import ru.sshibko.backend_seblog.model.repository.CommentVoteRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional()
public class CommentVoteService {


    private final CommentVoteRepository commentVoteRepository;

    private final CommentRepository commentRepository;

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @CacheEvict(value = {"commentVoteStats", "commentVoteCounts"}, key = "#commentId")
    public void voteForComment(UUID commentId, VoteRequest request) {
        log.info("voteForComment {}: {}", commentId, request.type());

        User currentUser = userService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.COMMENT_NOT_FOUND,
                        "Comment",
                        commentId,
                        "comment not found with ID: " + commentId));

        if (comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to vote this comment");
        }

        Optional<CommentVote> existingVote = commentVoteRepository.findByUserIdAndCommentId(
                currentUser.getId(), commentId);

        if (existingVote.isPresent()) {
            CommentVote commentVote = existingVote.get();
            if (commentVote.getType() == request.type()) {
                commentVoteRepository.delete(commentVote);
                log.info("voteForComment deleted:  user {} comment {}",
                        currentUser.getId(), commentId);
            } else {
                commentVote.setType(request.type());
                commentVoteRepository.save(commentVote);
                log.info("Vote updated: user {}, comment {}, type {}",
                        currentUser.getId(), commentId, request.type());
            }
        } else {
            CommentVote commentVote = CommentVote.builder()
                    .type(request.type())
                    .user(currentUser)
                    .comment(comment)
                    .build();

            commentVoteRepository.save(commentVote);
            log.info("Vote created: user {}, comment {}, type {}",
                    currentUser.getId(), commentId, request.type());
        }
    }

    @Transactional(readOnly = true)
    public VoteType getCurrentUserVoteForComment(UUID commentId) {
        User currentUser = userService.getCurrentUserOrNull();

        if (currentUser == null) {
            return null;
        }

        return commentVoteRepository.findByUserIdAndCommentId(currentUser.getId(), commentId)
                .map(CommentVote::getType)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "commentVoteCounts", key = "'likes: ' + #commentId")
    public Integer getCommentLikeCount(UUID commentId) {
        return commentVoteRepository.countByCommentIdAndType(commentId, VoteType.LIKE);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "commentVoteCounts", key = "'dilikes: ' + #commentId")
    public Integer getCommentDislikeCount(UUID commentId) {
        return commentVoteRepository.countByCommentIdAndType(commentId, VoteType.DISLIKE);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @CacheEvict(value = {"commentVoteStats", "commentVoteCounts"}, key = "#commentId")
    public void removeCommentVote(UUID commentId) {
        User currentUser = userService.getCurrentUser();

        CommentVote vote = commentVoteRepository.findByUserIdAndCommentId(currentUser.getId(), commentId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.VOTE_NOT_FOUND,
                        "CommentVote",
                        commentId,
                        "Vote not found"));

        commentVoteRepository.delete(vote);
        log.info("Vote deleted: user {}, comment {}", currentUser.getId(), commentId);
    }

    @Transactional(readOnly = true)
    public VoteStats getVoteStats(UUID commentId) {
        Integer likes = getCommentLikeCount(commentId);
        Integer dislikes = getCommentDislikeCount(commentId);
        VoteType userVote = getCurrentUserVoteForComment(commentId);

        return VoteStats.of(
                likes != null ? likes : 0,
                dislikes != null ? dislikes : 0,
                userVote
        );
    }
}
