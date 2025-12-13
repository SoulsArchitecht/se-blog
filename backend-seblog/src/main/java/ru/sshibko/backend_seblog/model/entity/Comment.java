package ru.sshibko.backend_seblog.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.sshibko.backend_seblog.model.entity.enums.CommentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comments", indexes = {
        @Index(columnList = "post_id"),
        @Index(columnList = "created_at"),
        @Index(columnList = "parent_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REMOVE, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @Column(name = "reply_count", columnDefinition = "INT DEFAULT 0")
    private Integer replyCount;

    @OneToMany(mappedBy = "comment", cascade = {CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REMOVE, CascadeType.REFRESH}, orphanRemoval = true)
    private List<CommentVote> votes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CommentStatus status;

/*    private boolean isDeleted;

    public boolean getIsDeleted() {
        return CommentStatus.DELETED.equals(status);
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }*/
}
