package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class PostDto {

    UUID id;
    String title;
    String content;
    String slug;
    String status;
    LocalDateTime publishedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserSummaryDto author;
    PostTypeDto type;
    Set<TagDto> tags;
    int upvotes;
    int downvotes;
    boolean userHasUpvoted;
    boolean userHasDownvoted;
    int commentCount;
    long viewCount;
}
