package ru.sshibko.backend_seblog.testdata;

import ru.sshibko.backend_seblog.dto.request.PostCreateRequest;
import ru.sshibko.backend_seblog.dto.request.PostUpdateRequest;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.PostType;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.PostStatus;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;

import java.util.Set;
import java.util.UUID;

public class PostTestData {

    private PostTestData() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User createAuthor() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("author")
                .email("author@example.com")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static User crateAnotherUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("somebody")
                .email("somebody@example.com")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static Post createPost(User author, PostType type) {
        return Post.builder()
                .id(UUID.randomUUID())
                .title("Original title")
                .slug("original-title")
                .content("<p>Original content</p>")
                .status(PostStatus.DRAFT)
                .author(author)
                .type(type)
                .viewCount(0L)
                .build();
    }

    public static PostType createPostType(String name) {
        return PostType.builder()
                .id(UUID.randomUUID())
                .name(name)
                .build();
    }

    public static PostCreateRequest createPostCreateRequest(String title, String customSlug, String postTypeName) {
        return PostCreateRequest.builder()
                .title(title)
                .content("<p>Test content</p>")
                .postTypeName(postTypeName)
                .status(PostStatus.DRAFT)
                .tagNames(Set.of("tag1", "tag2"))
                .customSlug(customSlug)
                .build();
    }

    public static PostUpdateRequest createPostUpdateRequest(
            String title, String content, String slug, String postTypeName) {
        return PostUpdateRequest.builder()
                .title(title)
                .content(content)
                .slug(slug)
                .postTypeName(postTypeName)
                .build();
    }
}
