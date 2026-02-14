package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UserDto {
    UUID id;
    String username;
    String email;
    String role;
}
