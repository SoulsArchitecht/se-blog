package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.model.entity.enums.CommentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findAllByPostIdAndStatus(UUID postId, CommentStatus status, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.post.id = :postId AND c.parent IS NULL AND c.status = :status " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findRootCommentsByPostId(@Param("postId") UUID postId, @Param("status") CommentStatus status);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.author " +
            "LEFT JOIN FETCH c.replies r " +
            "LEFT JOIN FETCH r.author " +
            "WHERE c.post.id = :postId AND c.parent IS NULL AND c.status = :status " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findCommentTreeByPostId(@Param("postId") UUID postId, @Param("status") CommentStatus status);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.author " +
            "LEFT JOIN FETCH c.replies " +
            "WHERE c.id = :id")
    Optional<Comment> findByIdWithDetails(@Param("id") UUID id);

    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);

    List<Comment> findAllByParentId(UUID parentId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.status = :status")
    Integer countByPostId(@Param("postId") UUID postId,  @Param("status") CommentStatus status);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.author.id = :authorId AND c.status NOT IN ('HIDDEN', 'DELETED')")
    long countVisibleCommentsByAuthor(@Param("authorId") UUID authorId);
}
