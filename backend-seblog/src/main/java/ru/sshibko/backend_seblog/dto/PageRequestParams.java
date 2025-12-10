package ru.sshibko.backend_seblog.dto;

public record PageRequestParams(
        int page,
        int size,
        String sortBy,
        String direction
) {
    public PageRequestParams {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (direction == null || direction.isBlank()) direction = "DESC";
    }
}
