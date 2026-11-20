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
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.PostVote;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;
import ru.sshibko.backend_seblog.model.repository.PostRepository;
import ru.sshibko.backend_seblog.model.repository.PostVoteRepository;
import ru.sshibko.backend_seblog.testdata.PostVoteTestData;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for PostVoteService")
class PostVoteServiceTest {

    @Mock private PostVoteRepository postVoteRepository;
    @Mock private PostRepository postRepository;
    @Mock private UserService userService;

    @InjectMocks
    private PostVoteService postVoteService;

    private User testVoter;
    private User testAuthor;
    private Post testPost;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        testVoter = PostVoteTestData.createVoter();
        testAuthor = PostVoteTestData.createAuthor();
        testPost = PostVoteTestData.createPost(testAuthor);

        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(userService.getCurrentUser()).thenReturn(testVoter);
        lenient().when(userService.getCurrentUserOrNull()).thenReturn(testVoter);
    }

    @Nested
    @DisplayName("Vote For Post Method")
    class VoteForPostTests {

        @Test
        @DisplayName("Should create new vote when user has not voted yet")
        void voteForPostShouldCreateNewVoteWhenNoExistingVote() {
            //Arrange
            VoteRequest request = PostVoteTestData.createVoteRequest(VoteType.LIKE);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(postVoteRepository.findByUserIdAndPostId(testVoter.getId(), testPost.getId()))
                    .thenReturn(Optional.empty());

            //Act
            postVoteService.voteForPost(testPost.getId(), request);

            //Assert
            ArgumentCaptor<PostVote> voteCaptor = ArgumentCaptor.forClass(PostVote.class);
            verify(postVoteRepository, times(1)).save(voteCaptor.capture());

            PostVote savedVote = voteCaptor.getValue();
            assertThat(savedVote.getType()).isEqualTo(VoteType.LIKE);
            assertThat(savedVote.getUser()).isEqualTo(testVoter);
            assertThat(savedVote.getPost()).isEqualTo(testPost);
        }

        @Test
        @DisplayName("Should delete vote when user votes with the same type (toggle off)")
        void voteForPostShouldDeleteVoteWhenVotingSameType() {
            //Arrange
            VoteRequest request = PostVoteTestData.createVoteRequest(VoteType.LIKE);
            PostVote existingVote = PostVoteTestData.createVote(testVoter, testPost, VoteType.LIKE);

            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(postVoteRepository.findByUserIdAndPostId(testVoter.getId(), testPost.getId()))
                    .thenReturn(Optional.of(existingVote));

            //Act
            postVoteService.voteForPost(testPost.getId(), request);

            //Assert
            verify(postVoteRepository, times(1)).delete(existingVote);
            verify(postVoteRepository, never()).save(any(PostVote.class));
        }

        @Test
        @DisplayName("Should update vote type when user changes their vote")
        void voteForPostShouldUpdateVoteTypeWhenVotingDifferentType() {
            // Arrange
            VoteRequest request = PostVoteTestData.createVoteRequest(VoteType.DISLIKE);
            PostVote existingVote = PostVoteTestData.createVote(testVoter, testPost, VoteType.LIKE);

            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(postVoteRepository.findByUserIdAndPostId(testVoter.getId(), testPost.getId()))
                    .thenReturn(Optional.of(existingVote));

            // Act
            postVoteService.voteForPost(testPost.getId(), request);

            // Assert
            verify(postVoteRepository, never()).delete(any(PostVote.class));

            ArgumentCaptor<PostVote> voteCaptor = ArgumentCaptor.forClass(PostVote.class);
            verify(postVoteRepository, times(1)).save(voteCaptor.capture());

            assertThat(voteCaptor.getValue().getType()).isEqualTo(VoteType.DISLIKE);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user tries to vote for own post")
        void voteForPostShouldThrownExceptionWhenVotingForOwnPost() {
            //Arrange
            VoteRequest request = PostVoteTestData.createVoteRequest(VoteType.LIKE);
            Post ownPost = PostVoteTestData.createPost(testVoter);

            when(postRepository.findById(ownPost.getId())).thenReturn(Optional.of(ownPost));

            //Act & Assert
            assertThatThrownBy(() -> postVoteService.voteForPost(ownPost.getId(), request))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("User is not allowed to vote for this post with id: "
                    + ownPost.getId());

            verify(postVoteRepository, never()).save(any());
            verify(postVoteRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Get Current User Vote Method")
    class GetCurrentUserVoteTests {

        @Test
        @DisplayName("Should return vote type when user is authenticated and has voted")
        void getCurrentUserVoteForPostShouldReturnTypeWhenVoted() {
            //Arrange
            PostVote existingVote = PostVoteTestData.createVote(testVoter, testPost, VoteType.DISLIKE);
            when(postVoteRepository.findByUserIdAndPostId(testVoter.getId(), testPost.getId()))
                    .thenReturn(Optional.of(existingVote));

            //Act
            VoteType result = postVoteService.getCurrentUserVoteForPost(testPost.getId());

            //Assert
            assertThat(result).isEqualTo(VoteType.DISLIKE);
        }

        @Test
        @DisplayName("Should return null when user is not authenticated")
        void getCurrentUserVoteForPostShouldReturnNullWhenNotAuthenticated() {
            //Arrange
            when(userService.getCurrentUserOrNull()).thenReturn(null);

            //Act
            VoteType result = postVoteService.getCurrentUserVoteForPost(testPost.getId());

            //Assert
            assertThat(result).isNull();
            verify(postVoteRepository, never()).findByUserIdAndPostId(any(), any());
        }
    }

    @Nested
    @DisplayName("Remove Post Vote Method")
    class RemovePostVoteMethod {

        @Test
        @DisplayName("Should delete vote successfully")
        void removePostVoteShouldDeleteVoteWhenExists() {
            //Arrange
            PostVote existingVote = PostVoteTestData.createVote(testVoter, testPost, VoteType.LIKE);
            when(postVoteRepository.findByUserIdAndPostId(testVoter.getId(), testPost.getId()))
                    .thenReturn(Optional.of(existingVote));

            //Act
            postVoteService.removePostVote(testPost.getId());

            //Assert
            verify(postVoteRepository, times(1)).delete(existingVote);
        }

        @Test
        @DisplayName("Should throw NotFoundException when vote does not exist")
        void removePostVoteShouldThrowExceptionWhenVoteNoteFound() {
            //Arrange
            when(postVoteRepository.findByUserIdAndPostId(testVoter.getId(), testPost.getId()))
                    .thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> postVoteService.removePostVote(testPost.getId()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("PostVote with identifier '" + testPost.getId()
                    + "' not found");

            verify(postVoteRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Get Post Vote Stats Method")
    class GetPostVoteStatsTests {

        @Test
        @DisplayName("Should calculate stats correctly for authenticated user")
        void getPostVoteStatsShouldCalculateCorrectlyWhenUserIsAuthenticated() {
            //Arrange
            PostVote userVote = PostVoteTestData.createVote(testVoter, testPost, VoteType.LIKE);
            when(postVoteRepository.countByPostIdAndType(testPost.getId(), VoteType.LIKE)).thenReturn(10);
            when(postVoteRepository.countByPostIdAndType(testPost.getId(), VoteType.DISLIKE)).thenReturn(3);
            when(postVoteRepository.findByUserIdAndPostId(testVoter.getId(), testPost.getId()))
                    .thenReturn(Optional.of(userVote));

            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.isAuthenticated()).thenReturn(true);
            when(mockAuth.getName()).thenReturn("authenticatedUser");
            when(securityContext.getAuthentication()).thenReturn(mockAuth);

            when(userService.getCurrentUser()).thenReturn(testVoter);

            //Act
            VoteStats stats = postVoteService.getPostVoteStats(testPost.getId());

            //Assert
            assertThat(stats.likesCount()).isEqualTo(10);
            assertThat(stats.dislikesCount()).isEqualTo(3);
            assertThat(stats.totalScore()).isEqualTo(7);
            assertThat(stats.userVote()).isEqualTo(VoteType.LIKE);
        }

        @Test
        @DisplayName("Should gracefully handle exceptions and return null userVote for anonymous")
        void getPostVoteStatsShouldGracefullyHandleExceptionWhenUserIsAnonymous() {
            //Arrange
            when(postVoteRepository.countByPostIdAndType(testPost.getId(), VoteType.LIKE)).thenReturn(5);
            when(postVoteRepository.countByPostIdAndType(testPost.getId(), VoteType.DISLIKE)).thenReturn(2);

            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.isAuthenticated()).thenReturn(false);
            when(securityContext.getAuthentication()).thenReturn(mockAuth);

            //Act
            VoteStats stats = postVoteService.getPostVoteStats(testPost.getId());

            //Assert
            assertThat(stats.likesCount()).isEqualTo(5);
            assertThat(stats.dislikesCount()).isEqualTo(2);
            assertThat(stats.totalScore()).isEqualTo(3);
            assertThat(stats.userVote()).isNull();

            verify(postVoteRepository, never()).findByUserIdAndPostId(any(), any());
        }
    }
}