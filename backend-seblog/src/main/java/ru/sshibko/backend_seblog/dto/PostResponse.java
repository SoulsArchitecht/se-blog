package ru.sshibko.backend_seblog.dto;

import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record PostResponse(
        UUID id,
        String title,
        String content,
        String slug,
        PostStatus status,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long viewCount,
        UUID authorId,
        PostTypeResponse type,
        Set<TagResponse> tags,
        Integer commentCount,
        Integer voteCount
) {

/*    public static PostResponseBuilder builder() {
        return new PostResponseBuilder();
    }

    public static class PostResponseBuilder {
        private UUID id;
        private String title;
        private String content;
        private String slug;
        private PostStatus status;
        private LocalDateTime publishedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long viewCount;
        private UUID authorId;
        private PostTypeResponse type;
        private Set<TagResponse> tags;
        private Integer commentCount;
        private Integer voteCount;

        public PostResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PostResponse build() {
            return new PostResponse(
                    id, title, content, slug, status, publishedAt,
                    createdAt, updatedAt, viewCount, authorId, type,
                    tags, commentCount, voteCount
            );
        }
    }*/
}
