package ru.sshibko.backend_seblog.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.sshibko.backend_seblog.exception.BaseException;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Schema(description = "Answer with error")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse (
        @Schema(description = "Error timestamp", example = "2026-01-15T10:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Error code", example = "ERR-0201")
        String errorCode,

        @Schema(description = "User message", example = "Invalid slug format")
        String userMessage,

        @Schema(description = "Developer message",
                example = "Slug should contain only Latin letters, numbers and hyphens")
        String developerMessage,

        @Schema(description = "Path request", example = "/api/v1/posts")
        String path,

        @Schema(description = "Error details (optional)")
        Map<String, Object> details,

        @Schema(description = "Request ID for tracking", example = "req-123456")
        String requestId

) {
    public static ErrorResponse fromException(BaseException ex, String path, String requestId) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .errorCode(ex.getErrorCode())
                .userMessage(ex.getUserMessage())
                .developerMessage(ex.getDeveloperMessage())
                .path(path)
                .details(ex.getDetails() instanceof Map ? (Map<String, Object>) ex.getDetails() : null)
                .requestId(requestId)
                .build();
    }

    public static ErrorResponse fromMessage(String errorCode, int status,                                                                              String userMessage, String developerMessage,
                                            String path, String requestId) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .errorCode(errorCode)
                .userMessage(userMessage)
                .developerMessage(developerMessage)
                .path(path)
                .requestId(requestId)
                .build();
    }
}