package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class UserSummaryDto {

    UUID id;
    String username;
    String displayName;
    String avatarUrl;
    LocalDateTime createAt;
}
