package ru.sshibko.backend_seblog.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class PostTypeDto {

    UUID id;
    String name;
    String slug;
    String colorHex;
    String icon;
}
