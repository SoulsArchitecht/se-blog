package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") UUID postId);

    List<Post> id(UUID id);

    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    @Query("""
    SELECT p FROM Post p
    JOIN FETCH p.author u
    LEFT JOIN FETCH u.profile
    JOIN FETCH p.type pt
    WHERE (:status IS NULL OR p.status = :status)
      AND (:typeSlug IS NULL OR pt.slug = :typeSlug)
      AND (:tagSlug IS NULL OR EXISTS (
           SELECT 1 FROM p.tags t WHERE t.slug = :tagSlug))
      AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<Post> findAllFiltered(
            @Param("status") PostStatus status,
            @Param("typeSlug") String typeSlug,
            @Param("tagSlug") String tagSlug,
            @Param("search") String search,
            Pageable pageable
    );
}
