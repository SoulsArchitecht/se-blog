package ru.sshibko.backend_seblog.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.PostTypeCreateRequest;
import ru.sshibko.backend_seblog.dto.response.PostTypeResponse;
import ru.sshibko.backend_seblog.model.entity.PostType;

@Service
@Slf4j
public class PostTypeMapperService {

    public PostTypeResponse mapToResponse(PostType postType) {

        log.info("before null check: {}", postType);
        if (postType == null) {
            return null;
        }

        log.info("after postType null check: {}", postType);


        log.info("{} {} {} {} {} {}", postType.getId(),
                postType.getName(),
                postType.getSlug(),
                postType.getIcon(),
                postType.getColorHex(),
                postType.getCreatedAt());

        return new PostTypeResponse(
                postType.getId(),
                postType.getName(),
                postType.getSlug(),
                postType.getIcon(),
                postType.getColorHex(),
                postType.getCreatedAt()
        );
/*
        return PostTypeResponse.builder()
                .id(postType.getId())
                .name(postType.getName())
                .slug(postType.getSlug())
                .icon(postType.getIcon())
                .colorHex(postType.getColorHex())
                .createdAt(postType.getCreatedAt())
                .build();
*/

    }

/*    public PostType toEntity(PostTypeCreateRequest request) {
        return PostType.builder()
                .name(request.name())
                .slug(request.slug())
                .icon(request.icon())
                .colorHex(request.colorHex())
                .build();
    }*/
}
