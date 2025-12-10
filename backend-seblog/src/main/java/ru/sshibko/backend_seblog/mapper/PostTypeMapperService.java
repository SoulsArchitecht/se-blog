package ru.sshibko.backend_seblog.mapper;

import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.PostTypeCreateRequest;
import ru.sshibko.backend_seblog.dto.PostTypeResponse;
import ru.sshibko.backend_seblog.model.entity.PostType;

@Service
public class PostTypeMapperService {

    public PostTypeResponse toResponse(PostType postType) {
        if (postType == null) {
            return null;
        }

        return new PostTypeResponse(
                postType.getId(),
                postType.getName(),
                postType.getSlug(),
                postType.getIcon(),
                postType.getColorHex(),
                postType.getCreatedAt()
        );
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
