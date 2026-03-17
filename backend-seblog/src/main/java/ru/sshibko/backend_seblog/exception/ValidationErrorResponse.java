package ru.sshibko.backend_seblog.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Schema(description = "Response with validation errors")
public record ValidationErrorResponse(
        @Schema(description = "Timestamp", example = "2024-01-15T10:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "HTTP code status", example = "400")
        int status,

        @Schema(description = "Error Code", example = "ERR-0002")
        String errorCode,

        @Schema(description = "Message", example = "Validation error")
        String message,

        @Schema(description = "Request path", example = "/api/v1/posts")
        String path,

        @Schema(description = "Errors by fields")
        Map<String, String> fieldErrors,

        @Schema(description = "Global Errors")
        Map<String, String> globalErrors,

        @Schema(description = "Request ID", example = "req-123456")
        String requestId
) {
    public static ValidationErrorResponse create(int status, String errorCode,
                                                 String message, String path,
                                                 Map<String, String> fieldErrors,
                                                 Map<String, String> globalErrors,
                                                 String requestId) {
            return ValidationErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status)
                    .errorCode(errorCode)
                    .message(message)
                    .path(path)
                    .fieldErrors(fieldErrors)
                    .globalErrors(globalErrors)
                    .requestId(requestId)
                    .build();
    }
}
