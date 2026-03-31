package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class UserProfileDto {

    UUID id;
    String displayName;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phone;
    String avatarUrl;
    String bio;
    String location;
    Integer rating;
}
