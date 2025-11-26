package ru.sshibko.backend_seblog.dto.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    String refreshToken;
}
