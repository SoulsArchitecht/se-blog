package ru.sshibko.backend_seblog.dto;

import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostFilterRequest(
        String search,
        PostStatus status,
        UUID authorId,
        UUID tagId,
        UUID typeId,
        LocalDateTime publishedAfter,
        LocalDateTime publishedBefore,
        Boolean hasComments,
        Integer minViews
) {
    public PostFilterRequest {
        if (search != null && search.isBlank()) {
            search = null;
        }
    }
}
