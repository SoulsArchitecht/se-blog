package ru.sshibko.backend_seblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;

import java.util.Set;
import java.util.UUID;

public record PostCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Content is required")
        String content,

        @NotNull(message = "Post type is required")
        UUID postTypeId,

        PostStatus status,

        Set<@NotBlank String> tagNames
) {
    public PostCreateRequest {
        if (status == null) {
            status = PostStatus.DRAFT;
        }
        if (tagNames == null) {
            tagNames = Set.of();
        }
    }
}
