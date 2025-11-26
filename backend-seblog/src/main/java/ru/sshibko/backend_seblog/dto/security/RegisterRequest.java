package ru.sshibko.backend_seblog.dto.security;


import lombok.Value;

@Value
public class RegisterRequest {

    String username;
    String password;
    String email;
}
