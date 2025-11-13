package ru.sshibko.backend_seblog.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment_votes", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_comment_voter",
                columnNames = {"comment_id", "voter_id"}
        )
}, indexes = {
        @Index(name = "idx_comment_votes_comment", columnList = "comment_id"),
        @Index(name = "idx_comment_votes_voter", columnList = "voter_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentVote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private PostComment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User voter;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private VoteType type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
