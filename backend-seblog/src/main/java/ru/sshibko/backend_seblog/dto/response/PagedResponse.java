package ru.sshibko.backend_seblog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Schema(description = "Пагинированный ответ")
public record PagedResponse<T>(
        @Schema(description = "Номер страницы",
                example = "0")
        int page,

        @Schema(description = "Размер страницы",
                example = "10")
        int size,

        @Schema(description = "Всего элементов",
                example = "100")
        long totalElements,

        @Schema(description = "Всего страниц",
                example = "10")
        int totalPages,

        @Schema(description = "Это последняя страница?",
                example = "false")
        boolean last,

        @Schema(description = "Содержимое")
        List<T> content
) {
    public static <T> PagedResponse<T> of(Page<T> page) {
        return PagedResponse.<T>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(page.getContent())
                .build();
    }
}