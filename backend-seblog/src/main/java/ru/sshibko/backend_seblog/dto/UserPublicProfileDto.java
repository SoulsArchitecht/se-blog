package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class UserPublicProfileDto {
    UUID id;
    String username;
    String displayName;
    String location;
    String avatarUrl;
    String bio;
    LocalDateTime memberSince;
    Long postCount;
    Long commentCount;
    Integer rating;
}
