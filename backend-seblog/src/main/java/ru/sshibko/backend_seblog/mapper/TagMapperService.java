package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.TagCreateRequest;
import ru.sshibko.backend_seblog.dto.response.TagResponse;
import ru.sshibko.backend_seblog.dto.TagUpdateRequest;
import ru.sshibko.backend_seblog.model.entity.Tag;
import ru.sshibko.backend_seblog.service.SlugService;

@Service
@RequiredArgsConstructor
public class TagMapperService {

    private final SlugService slugService;

    public TagResponse mapToResponse(Tag tag) {
        if (tag == null) {
            return null;
        }

        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                //.slug(slugService.generateSlug(tag.getName()))
                .createdAt(tag.getCreatedAt())
                .postCount(tag.getPosts().size())
                .build();
    }
}
