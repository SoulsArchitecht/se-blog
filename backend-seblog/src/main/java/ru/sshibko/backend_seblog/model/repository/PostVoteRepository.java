package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.PostVote;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, UUID> {

    Optional<PostVote> findByUserIdAndPostId(UUID userId, UUID postId);

    @Query("SELECT COUNT(pv) FROM PostVote pv WHERE pv.post.id = :postId AND pv.type = :type")
    Integer countByPostIdAndType(@Param("postId") UUID postId, @Param("type") VoteType type);

    @Query("SELECT pv FROM PostVote pv WHERE pv.user.id = :userId AND pv.post.id = :postId")
    Optional<PostVote> findUserVoteForPost(@Param("userId") UUID userId, @Param("postId") UUID postId);

    boolean existsByUserIdAndPostId(UUID userId, UUID postId);

    void deleteByUserIdAndPostId(UUID userId, UUID postId);
}
