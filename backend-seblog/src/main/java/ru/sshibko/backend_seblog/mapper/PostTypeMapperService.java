package ru.sshibko.backend_seblog.mapper;

import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.PostTypeCreateRequest;
import ru.sshibko.backend_seblog.dto.response.PostTypeResponse;
import ru.sshibko.backend_seblog.model.entity.PostType;

@Service
public class PostTypeMapperService {

    public PostTypeResponse mapToResponse(PostType postType) {
        if (postType == null) {
            return null;
        }

        return PostTypeResponse.builder()
                .id(postType.getId())
                .name(postType.getName())
                .slug(postType.getSlug())
                .icon(postType.getIcon())
                .colorHex(postType.getColorHex())
                .createdAt(postType.getCreatedAt())
                .build();

    }

    public PostType toEntity(PostTypeCreateRequest request) {
        return PostType.builder()
                .name(request.name())
                .slug(request.slug())
                .icon(request.icon())
                .colorHex(request.colorHex())
                .build();
    }
}
