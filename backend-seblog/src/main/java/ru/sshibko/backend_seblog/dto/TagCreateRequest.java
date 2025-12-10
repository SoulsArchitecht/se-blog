package ru.sshibko.backend_seblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TagCreateRequest(
        @NotBlank(message = "Tag name is required")
        @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\s\\-]+$",
                message = "Tag name can only contain letters, numbers, spaces and hyphens")
        String name
) {}
