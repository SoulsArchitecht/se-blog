package ru.sshibko.backend_seblog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.TagCreateRequest;
import ru.sshibko.backend_seblog.dto.TagResponse;
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
        return new TagResponse(
                tag.getId(),
                tag.getName(),
                slugService.generateSlug(tag.getName()),
                tag.getCreatedAt(),
                tag.getPosts().size()
        );
    }

    public Tag toEntity(TagCreateRequest request) {
        return Tag.builder()
                .name(request.name().trim())
                .build();
    }

    public void updateEntityFromRequest(Tag tag, TagUpdateRequest request) {
        if (request.name() != null && !request.name().isBlank()) {
            tag.setName(request.name().trim());
        }
    }

    public TagResponse toSimpleResponse(Tag tag) {
        if (tag == null) {
            return null;
        }

        return new TagResponse(
                tag.getId(),
                tag.getName(),
                slugService.generateSlug(tag.getName()),
                tag.getCreatedAt(),
                null
        );
    }
}
