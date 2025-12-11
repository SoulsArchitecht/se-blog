package ru.sshibko.backend_seblog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

@Builder
@Schema(description = "Запрос на голосование")
public record VoteRequest(
        @Schema(description = "Тип голоса",
                example = "LIKE",
                requiredMode = Schema.RequiredMode.REQUIRED)
        VoteType type
) {
}
