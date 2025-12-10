package ru.sshibko.backend_seblog.dto;

public record PagedResponse<T>(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last,
        Iterable<T> content
) {
    public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> page) {
        return new PagedResponse<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.getContent()
        );
    }
}
