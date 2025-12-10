package ru.sshibko.backend_seblog.dto;

import jakarta.validation.constraints.NotBlank;
import ru.sshibko.backend_seblog.aop.annotation.ValidSlug;

public record PostTypeCreateRequest(
        @NotBlank String name,

        @NotBlank
        @ValidSlug
        String slug,

        @NotBlank String icon,

        String colorHex
) {}
