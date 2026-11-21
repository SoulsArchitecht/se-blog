package ru.sshibko.backend_seblog.testdata;

import ru.sshibko.backend_seblog.dto.request.VoteRequest;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.model.entity.CommentVote;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;

import java.util.UUID;

public final class CommentVoteTestData {

    public CommentVoteTestData() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User createVoter() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("voter")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static User createAuthor() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("author")
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static Comment createComment(User author) {
        return Comment.builder()
                .id(UUID.randomUUID())
                .content("Test comment")
                .author(author)
                .build();
    }

    public static CommentVote createVote(User user, Comment comment, VoteType type) {
        return CommentVote.builder()
                .id(UUID.randomUUID())
                .user(user)
                .comment(comment)
                .type(type)
                .build();
    }

    public static VoteRequest createVoteRequest(VoteType type) {
        return new VoteRequest(type);
    }
}
