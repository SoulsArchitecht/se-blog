package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;
import ru.sshibko.backend_seblog.dto.response.UserSummaryResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class CommentDto {

    UUID id;
    String content;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserSummaryResponse author;
    UUID postId;
    UUID parentId;
    Set<CommentDto> replies;
    int upvotes;
    int downvotes;
    boolean userHasUpvoted;
    boolean userHasDownvoted;
}
