package ru.sshibko.backend_seblog.testdata;

import ru.sshibko.backend_seblog.dto.request.VoteRequest;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.PostVote;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.util.UUID;

public final class PostVoteTestData {

    public PostVoteTestData() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User createVoter() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("voter")
                .email("voter@example.com")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
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

    public static Post createPost(User author) {
        return Post.builder()
                .id(UUID.randomUUID())
                .title("Test Post")
                .author(author)
                .build();
    }

    public static PostVote createVote(User user, Post post, VoteType type) {
        return PostVote.builder()
                .id(UUID.randomUUID())
                .user(user)
                .post(post)
                .type(type)
                .build();
    }

    public static VoteRequest createVoteRequest(VoteType type) {
        return new VoteRequest(type);
    }
}
