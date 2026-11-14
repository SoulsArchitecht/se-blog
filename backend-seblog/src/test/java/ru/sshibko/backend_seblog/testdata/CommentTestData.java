package ru.sshibko.backend_seblog.testdata;

import ru.sshibko.backend_seblog.dto.request.CommentCreateRequest;
import ru.sshibko.backend_seblog.dto.request.CommentUpdateRequest;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.CommentStatus;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public final class CommentTestData {

    private CommentTestData() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User createAuthor() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("commenter")
                .email("commenter@example.com")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static Post createPost() {
        return Post.builder()
                .id(UUID.randomUUID())
                .title("Test Post")
                .slug("test-post")
                .build();
    }

    public static Comment createRootComment(Post post, User author) {
        return Comment.builder()
                .id(UUID.randomUUID())
                .content("This is a root comment")
                .author(author)
                .post(post)
                .parent(null)
                .status(CommentStatus.ACTIVE)
                .replyCount(0)
                .replies(new ArrayList<>())
                .build();
    }

    public static Comment createParentCommentWithReplies(Post post, User author) {
        Comment parent = createRootComment(post, author);
        parent.setReplyCount(1);
        Comment reply = Comment.builder()
                .id(UUID.randomUUID())
                .content("This is a reply")
                .author(author)
                .post(post)
                .parent(parent)
                .status(CommentStatus.ACTIVE)
                .build();
        parent.getReplies().add(reply);
        return parent;
    }

    public static CommentCreateRequest createCommentRequest(String content, UUID parentId) {
        return new CommentCreateRequest(content, parentId);
    }

    public static CommentUpdateRequest createUpdateRequest(String newContent) {
        return new CommentUpdateRequest(newContent);
    }
}
