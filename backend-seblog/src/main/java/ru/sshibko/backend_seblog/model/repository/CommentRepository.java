package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findAllByPostIdAndIsDeletedFalse(UUID postId, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.post.id = :postId AND c.parent IS NULL AND c.isDeleted = false " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findRootCommentsByPostId(@Param("postId") UUID postId);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.author " +
            "LEFT JOIN FETCH c.replies r " +
            "LEFT JOIN FETCH r.author " +
            "WHERE c.post.id = :postId AND c.parent IS NULL AND c.isDeleted = false " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findCommentTreeByPostId(@Param("postId") UUID postId);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.author " +
            "LEFT JOIN FETCH c.replies " +
            "WHERE c.id = :id")
    Optional<Comment> findByIdWithDetails(@Param("id") UUID id);

    Optional<Comment> findByIdAndPostId(UUID id, UUID postId);

    List<Comment> findAllByParentId(UUID parentId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.isDeleted = false")
    Integer countByPostId(@Param("postId") UUID postId);
}
