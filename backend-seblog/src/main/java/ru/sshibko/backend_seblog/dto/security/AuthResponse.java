package ru.sshibko.backend_seblog.dto.security;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;

@Value
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthResponse {

    String accessToken;
    String refreshToken;
    String tokenType = "Bearer";
}
