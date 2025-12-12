package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.CommentVote;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, UUID> {

    Optional<CommentVote> findByUserIdAndCommentId(UUID userId, UUID commentId);

    @Query("SELECT COUNT(cv) FROM CommentVote cv WHERE cv.comment.id = :commentId AND cv.type = :type")
    Integer countByCommentIdAndType(@Param("commentId") UUID commentId, @Param("type") VoteType type);

    @Query("SELECT cv FROM CommentVote cv WHERE cv.user.id = :userId AND cv.comment.id = :commentId")
    Optional<CommentVote> findUserVoteForComment(@Param("userId") UUID userId, @Param("commentId") UUID commentId);

    boolean existsByUserIdAndCommentId(UUID userId, UUID commentId);

    void deleteByUserIdAndCommentId(UUID userId, UUID commentId);
}
