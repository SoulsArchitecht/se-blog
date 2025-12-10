package ru.sshibko.backend_seblog.dto.security;

import lombok.Value;

@Value
public class AuthRequest {

    String email;
    String password;
}
