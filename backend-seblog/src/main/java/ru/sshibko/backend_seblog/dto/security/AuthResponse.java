package ru.sshibko.backend_seblog.dto.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sshibko.backend_seblog.dto.UserDto;

@Value
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthResponse {

    //@JsonProperty("token")
    String accessToken;

    //@JsonProperty("refreshToken")
    String refreshToken;

    String tokenType = "Bearer";
    UserDto user;
}
