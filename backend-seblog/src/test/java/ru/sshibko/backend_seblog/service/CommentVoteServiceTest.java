package ru.sshibko.backend_seblog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.sshibko.backend_seblog.dto.VoteStats;
import ru.sshibko.backend_seblog.dto.request.VoteRequest;
import ru.sshibko.backend_seblog.exception.AccessDeniedException;
import ru.sshibko.backend_seblog.exception.NotFoundException;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.model.entity.CommentVote;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;
import ru.sshibko.backend_seblog.model.repository.CommentRepository;
import ru.sshibko.backend_seblog.model.repository.CommentVoteRepository;
import ru.sshibko.backend_seblog.testdata.CommentVoteTestData;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test for CommentVoteService")
class CommentVoteServiceTest {

    @Mock private CommentVoteRepository commentVoteRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserService userService;

    @InjectMocks CommentVoteService commentVoteService;

    private User testVoter;
    private User testAuthor;
    private Comment testComment;
    private SecurityContext securityContext;

    @BeforeEach()
    void setUp() {
        testVoter = CommentVoteTestData.createVoter();
        testAuthor = CommentVoteTestData.createAuthor();
        testComment = CommentVoteTestData.createComment(testAuthor);

        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(userService.getCurrentUser()).thenReturn(testVoter);
        lenient().when(userService.getCurrentUserOrNull()).thenReturn(testVoter);
    }

    @Nested
    @DisplayName("Vote For Comment Method")
    class VoteForCommentTests {

        @Test
        @DisplayName("Should create new vote when user has not voted yet")
        void voteForCommentShouldCreateNewVoteWhenNoExistingVote() {
            //Arrange
            VoteRequest request = CommentVoteTestData.createVoteRequest(VoteType.LIKE);
            when(commentRepository.findById(testComment.getId())).thenReturn(Optional.of(testComment));
            when(commentVoteRepository.findByUserIdAndCommentId(testVoter.getId(), testComment.getId()))
                    .thenReturn(Optional.empty());

            //Act
            commentVoteService.voteForComment(testComment.getId(), request);

            //Assert
            ArgumentCaptor<CommentVote> voteCaptor = ArgumentCaptor.forClass(CommentVote.class);
            verify(commentVoteRepository, times(1)).save(voteCaptor.capture());

            CommentVote savedVote = voteCaptor.getValue();
            assertThat(savedVote.getType()).isEqualTo(VoteType.LIKE);
            assertThat(savedVote.getUser()).isEqualTo(testVoter);
            assertThat(savedVote.getComment()).isEqualTo(testComment);
        }

        @Test
        @DisplayName("Should delete vote when user votes with the same type (toggle off)")
        void voteForCommentShouldDeleteVoteWhenVotingSameType() {
            // Arrange
            VoteRequest request = CommentVoteTestData.createVoteRequest(VoteType.LIKE);
            CommentVote existingVote = CommentVoteTestData.createVote(testVoter, testComment, VoteType.LIKE);

            when(commentRepository.findById(testComment.getId())).thenReturn(Optional.of(testComment));
            when(commentVoteRepository.findByUserIdAndCommentId(testVoter.getId(), testComment.getId()))
                    .thenReturn(Optional.of(existingVote));

            // Act
            commentVoteService.voteForComment(testComment.getId(), request);

            // Assert
            verify(commentVoteRepository, times(1)).delete(existingVote);
            verify(commentVoteRepository, never()).save(any(CommentVote.class));
        }

        @Test
        @DisplayName("Should update vote type when user changes their vote")
        void voteForCommentShouldUpdateVoteTypeWhenVotingDifferentType() {
            //Arrange
            VoteRequest request = CommentVoteTestData.createVoteRequest(VoteType.DISLIKE);
            CommentVote existingVote = CommentVoteTestData.createVote(testVoter, testComment, VoteType.LIKE);

            when(commentRepository.findById(testComment.getId())).thenReturn(Optional.of(testComment));
            when(commentVoteRepository.findByUserIdAndCommentId(testVoter.getId(), testComment.getId()))
                    .thenReturn(Optional.of(existingVote));

            //Act
            commentVoteService.voteForComment(testComment.getId(), request);

            //Assert
            verify(commentVoteRepository, never()).delete(any(CommentVote.class));

            ArgumentCaptor<CommentVote> voteCaptor = ArgumentCaptor.forClass(CommentVote.class);
            verify(commentVoteRepository, times(1)).save(voteCaptor.capture());

            assertThat(voteCaptor.getValue().getType()).isEqualTo(VoteType.DISLIKE);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user tries to vote for onw comment")
        void voteForCommentShouldThrowExceptionWhenVotingForOwnComment() {
            //Arrange
            VoteRequest request = CommentVoteTestData.createVoteRequest(VoteType.LIKE);
            Comment ownComment = CommentVoteTestData.createComment(testVoter);

            when(commentRepository.findById(ownComment.getId())).thenReturn(Optional.of(ownComment));

            //Act & Assert
            assertThatThrownBy(() -> commentVoteService.voteForComment(ownComment.getId(), request))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("User is not allowed to vote for this comment with id: "
                    + ownComment.getId());

            verify(commentVoteRepository, never()).save(any());
            verify(commentVoteRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Get Current User Vote Method")
    class GetCurrentUserVoteTests {

        @Test
        @DisplayName("Should return vote type when user is authenticated and has voted")
        void getCurrentUserVoteForCommentShouldReturnTypeWhenVoted() {
            //Arrange
            CommentVote existingVote = CommentVoteTestData.createVote(testVoter, testComment, VoteType.DISLIKE);
            when(commentVoteRepository.findByUserIdAndCommentId(testVoter.getId(), testComment.getId()))
                    .thenReturn(Optional.of(existingVote));

            //Act
            VoteType result = commentVoteService.getCurrentUserVoteForComment(testComment.getId());

            //Assert
            assertThat(result).isEqualTo(VoteType.DISLIKE);
        }

        @Test
        @DisplayName("Should return null when user in not authenticated")
        void getCurrentUserVoteForCommentShouldReturnNullWhenNotAuthenticated() {
            //Arrange
            when(userService.getCurrentUserOrNull()).thenReturn(null);

            //Act
            VoteType result = commentVoteService.getCurrentUserVoteForComment(testComment.getId());

            //Assert
            assertThat(result).isNull();
            verify(commentVoteRepository, never()).findByUserIdAndCommentId(any(), any());
        }
    }

    @Nested
    @DisplayName("Remove Comment Vote Method")
    class RemoveCommentVoteTests {

        @Test
        @DisplayName("Should delete vote successfully")
        void removeCommentVoteShouldDeleteVoteWhenExists() {
            //Arrange
            CommentVote existingVote = CommentVoteTestData.createVote(testVoter, testComment, VoteType.LIKE);
            when(commentVoteRepository.findByUserIdAndCommentId(testVoter.getId(), testComment.getId()))
                    .thenReturn(Optional.of(existingVote));

            //Act
            commentVoteService.removeCommentVote(testComment.getId());

            //Assert
            verify(commentVoteRepository, times(1)).delete(existingVote);
        }

        @Test
        @DisplayName("Should throw NotFoundException when vote does not exist")
        void removeCommentVoteShouldThrowExceptionWhenVoteNotFound() {
            //Arrange
            when(commentVoteRepository.findByUserIdAndCommentId(testVoter.getId(), testComment.getId()))
                    .thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> commentVoteService.removeCommentVote(testComment.getId()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("CommentVote with identifier '"
                            + testComment.getId() + "' not found");

            verify(commentVoteRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Get Comment Vote Stats Method")
    class GetCommentVoteStatsTests {

        @Test
        @DisplayName("Should calculate stats correctly for authenticated user")
        void getCommentVoteStatsShouldCalculateCorrectlyWhenUserIsAuthenticated() {
            //Arrange
            CommentVote userVote = CommentVoteTestData.createVote(testVoter, testComment, VoteType.LIKE);
            when(commentVoteRepository.countByCommentIdAndType(testComment.getId(), VoteType.LIKE))
                    .thenReturn(10);
            when(commentVoteRepository.countByCommentIdAndType(testComment.getId(), VoteType.DISLIKE))
                    .thenReturn(3);
            when(commentVoteRepository.findByUserIdAndCommentId(testVoter.getId(), testComment.getId()))
                    .thenReturn(Optional.of(userVote));

            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.isAuthenticated()).thenReturn(true);
            when(mockAuth.getName()).thenReturn("authenticatedUser");
            when(securityContext.getAuthentication()).thenReturn(mockAuth);

            //Act
            VoteStats stats = commentVoteService.getCommentVoteStats(testComment.getId());

            //Assert
            assertThat(stats.likesCount()).isEqualTo(10);
            assertThat(stats.dislikesCount()).isEqualTo(3);
            assertThat(stats.totalScore()).isEqualTo(7);
            assertThat(stats.userVote()).isEqualTo(VoteType.LIKE);
        }

        @Test
        @DisplayName("Should gracefully handle exceptions and return null userVote for anonymous")
        void getCommentVoteStatsShouldGracefullyHandleExceptionWhenUserIsAnonymous() {
            //Arrange
            when(commentVoteRepository.countByCommentIdAndType(testComment.getId(), VoteType.LIKE))
                    .thenReturn(5);
            when(commentVoteRepository.countByCommentIdAndType(testComment.getId(), VoteType.DISLIKE))
                    .thenReturn(3);

            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.isAuthenticated()).thenReturn(false);
            when(securityContext.getAuthentication()).thenReturn(mockAuth);

            //Act
            VoteStats stats = commentVoteService.getCommentVoteStats(testComment.getId());

            //Assert
            assertThat(stats.likesCount()).isEqualTo(5);
            assertThat(stats.dislikesCount()).isEqualTo(3);
            assertThat(stats.totalScore()).isEqualTo(2);
            assertThat(stats.userVote()).isNull();

            verify(commentVoteRepository, never()).findByUserIdAndCommentId(any(), any());
        }
    }

}