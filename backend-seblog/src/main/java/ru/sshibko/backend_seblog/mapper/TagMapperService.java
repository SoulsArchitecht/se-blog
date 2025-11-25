package ru.sshibko.backend_seblog.mapper;

import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.TagDto;
import ru.sshibko.backend_seblog.model.entity.Tag;

@Service
public class TagMapperService {

    public TagDto toDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .build();
    }
}
