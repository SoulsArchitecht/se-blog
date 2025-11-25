package ru.sshibko.backend_seblog.mapper;

import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.PostTypeDto;
import ru.sshibko.backend_seblog.model.entity.PostType;

@Service
public class PostTypeMapperService {

    public PostTypeDto toDto(PostType type) {
        if (type == null) return null;
        return PostTypeDto.builder()
                .id(type.getId())
                .name(type.getName())
                .slug(type.getSlug())
                .colorHex(type.getColorHex())
                .icon(type.getIcon())
                .build();
    }
}
