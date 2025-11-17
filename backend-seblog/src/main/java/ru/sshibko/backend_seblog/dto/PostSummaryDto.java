package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class PostSummaryDto {

    UUID id;
    String title;
    String slug;
    UserSummaryDto author;
    PostTypeDto type;
    LocalDateTime createdAt;
    int commentCount;
    int upvotes;
    long viewCount;
}
