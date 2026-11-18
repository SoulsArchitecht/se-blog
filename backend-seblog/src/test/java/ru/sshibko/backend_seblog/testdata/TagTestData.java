package ru.sshibko.backend_seblog.testdata;

import ru.sshibko.backend_seblog.dto.request.TagCreateRequest;
import ru.sshibko.backend_seblog.model.entity.Tag;

import java.util.UUID;

public final class TagTestData {

    private TagTestData() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Tag createTag(String name) {
        return Tag.builder()
                .id(UUID.randomUUID())
                .name(name)
                .build();
    }

    public static TagCreateRequest createTagRequest(String name) {
        return new TagCreateRequest(name);
    }
}
