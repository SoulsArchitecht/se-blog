package ru.sshibko.backend_seblog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostTypeResponse(
        UUID id,
        String name,
        String slug,
        String icon,
        String colorHex,
        LocalDateTime createdAt
) {}
