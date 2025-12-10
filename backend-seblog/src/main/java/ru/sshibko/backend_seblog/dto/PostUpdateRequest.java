package ru.sshibko.backend_seblog.dto;

import jakarta.validation.constraints.Size;
import ru.sshibko.backend_seblog.aop.annotation.ValidSlug;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;

import java.util.Set;
import java.util.UUID;

public record PostUpdateRequest(
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        String content,

        @ValidSlug
        String slug,

        PostStatus status,

        UUID postTypeId,

        Set<String> tagNames
) {}
