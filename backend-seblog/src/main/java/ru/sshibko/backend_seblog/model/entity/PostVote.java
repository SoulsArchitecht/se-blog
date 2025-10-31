package ru.sshibko.backend_seblog.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_votes", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_post_voter",
                columnNames = {"post_id", "voter_id"}
        )
}, indexes = {
        @Index(name = "idx_post_votes_post", columnList = "post_id"),
        @Index(name = "idx_post_votes_voter", columnList = "voter_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VoteType type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
