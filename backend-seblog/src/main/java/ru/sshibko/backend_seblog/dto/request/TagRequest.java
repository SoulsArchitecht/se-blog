package ru.sshibko.backend_seblog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Запрос имени тэга")
public record TagRequest (
        @Schema(description = "имя тэга",
                example = "windows")
        String name
) {}
