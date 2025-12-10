package ru.sshibko.backend_seblog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        String slug,
        LocalDateTime createdAt,
        Integer postCount
) {}
