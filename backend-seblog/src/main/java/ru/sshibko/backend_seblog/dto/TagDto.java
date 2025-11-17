package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class TagDto {

    UUID id;
    String name;
    String slug;
}
