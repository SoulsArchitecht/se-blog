package ru.sshibko.backend_seblog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sshibko.backend_seblog.dto.request.CommentCreateRequest;
import ru.sshibko.backend_seblog.dto.request.CommentUpdateRequest;
import ru.sshibko.backend_seblog.dto.response.CommentResponse;
import ru.sshibko.backend_seblog.exception.ValidationException;
import ru.sshibko.backend_seblog.mapper.CommentMapperService;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.CommentStatus;
import ru.sshibko.backend_seblog.model.repository.CommentRepository;
import ru.sshibko.backend_seblog.model.repository.PostRepository;
import ru.sshibko.backend_seblog.testdata.CommentTestData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test for CommentService")
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private UserService userService;
    @Mock private CommentVoteService commentVoteService;
    @Mock private CommentMapperService commentMapperService;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private Comment testRootComment;
    private Comment testParentCommentWithReplies;
    private CommentResponse mockCommentResponse;

    @BeforeEach
    void setUp() {
        testUser = CommentTestData.createAuthor();
        testPost = CommentTestData.createPost();
        testRootComment = CommentTestData.createRootComment(testPost, testUser);
        testParentCommentWithReplies = CommentTestData.createParentCommentWithReplies(testPost, testUser);
        mockCommentResponse = mock(CommentResponse.class);

        //when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Nested
    @DisplayName("Create Comment Method")
    class CreateCommentTests {

        @Test
        @DisplayName("Should create root comment successfully")
        void createCommentShouldCreateRootCommentWhenValidRequest() {
            //Arrange
            CommentCreateRequest request = CommentTestData.createCommentRequest(
                    "New root comment", null);
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(commentRepository.save(any(Comment.class))).thenReturn(testRootComment);
            when(commentMapperService.mapToResponse(any(Comment.class))).thenReturn(mockCommentResponse);

            //Act
            CommentResponse response = commentService.createComment(testPost.getId(), request);

            //Assert
            assertThat(response).isEqualTo(mockCommentResponse);

            ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
            verify(commentRepository, times(1)).save(commentCaptor.capture());

            Comment savedComment = commentCaptor.getValue();
            assertThat(savedComment.getContent()).isEqualTo("New root comment");
            assertThat(savedComment.getParent()).isNull();
            assertThat(savedComment.getStatus()).isEqualTo(CommentStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should create reply and increment parent replyCount")
        void createCommentShouldCreateReplyAndIncrementCountWhenParentIdProvided() {
            //Arrange
            UUID parentId = testParentCommentWithReplies.getId();
            CommentCreateRequest request = CommentTestData.createCommentRequest("A reply", parentId);

            when(userService.getCurrentUser()).thenReturn(testUser);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(commentRepository.findById(parentId)).thenReturn(Optional.of(testParentCommentWithReplies));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
                Comment saved = invocation.getArgument(0);
                if (saved.getId() == null) {
                    saved.setId(UUID.randomUUID());
                }
                return saved;
            });
            when(commentMapperService.mapToResponse(any(Comment.class))).thenReturn(mockCommentResponse);

            //Act
            commentService.createComment(testPost.getId(), request);

            //Assert
            ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

            verify(commentRepository, times(2)).save(captor.capture());

            List<Comment> savedComments = captor.getAllValues();

            Comment savedParent = savedComments.get(0);
            assertThat(savedParent.getId()).isEqualTo(parentId);
            assertThat(savedParent.getReplyCount()).isEqualTo(2);

            Comment savedNewComment = savedComments.get(1);
            assertThat(savedNewComment.getContent()).isEqualTo("A reply");
            assertThat(savedNewComment.getParent()).isNotNull();
            assertThat(savedNewComment.getParent().getId()).isEqualTo(parentId);
/*            //1. verify counter
            verify(commentRepository, times(1)).save(argThat(comment ->
                    comment.getId().equals(parentId) && comment.getReplyCount() == 2));
            //2. verify parent
            verify(commentRepository, times(2)).save(any(Comment.class));*/
        }

        @Test
        @DisplayName("Should throw ValidationException when parent belongs to different post")
        void createCommentShouldThrowExceptionWhenParentPostMismatch() {
            //Arrange
            Post differentPost = CommentTestData.createPost();
            Comment parentOnDifferentPost = CommentTestData.createRootComment(differentPost, testUser);
            CommentCreateRequest request = CommentTestData.createCommentRequest(
                    "Reply", parentOnDifferentPost.getId());
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(commentRepository.findById(
                    parentOnDifferentPost.getId())).thenReturn(Optional.of(parentOnDifferentPost));

            //Act & Assert
            assertThatThrownBy(() -> commentService.createComment(testPost.getId(), request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining(
                            "Parent comment with id: " + parentOnDifferentPost.getId() +
                                    " belongs to post " + differentPost.getId() +
                            ", not " + testPost.getId()
                    );

            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("Update Comment Method")
    class UpdateCommentTests {

        @Test
        @DisplayName("Should update comment content successfully")
        void updateCommentShouldUpdateContentWhenCommentIsActive() {
            //Arrange
            CommentUpdateRequest request = CommentTestData.createUpdateRequest("Updated content");
            when(commentRepository.findByIdAndPostId(testRootComment.getId(), testPost.getId()))
                    .thenReturn(Optional.of(testRootComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testRootComment);
            when(commentMapperService.mapToResponse(any(Comment.class))).thenReturn(mockCommentResponse);

            //Act
            CommentResponse response = commentService.updateComment(
                    testPost.getId(), testRootComment.getId(), request);

            //Assert
            assertThat(response).isEqualTo(mockCommentResponse);
            assertThat(testRootComment.getContent()).isEqualTo("Updated content");
            verify(commentRepository, times(1)).save(testRootComment);
        }

        @Test
        @DisplayName("Should throw ValidationException when trying to update deleted comment")
        void updateCommentShouldThrowExceptionWhenCommentIsDeleted() {
            //Arrange
            testRootComment.setStatus(CommentStatus.DELETED);
            CommentUpdateRequest request = CommentTestData.createUpdateRequest("Updated content");

            when(commentRepository.findByIdAndPostId(testRootComment.getId(), testPost.getId()))
                    .thenReturn(Optional.of(testRootComment));

            //Act  & Assert
            assertThatThrownBy(() -> commentService.updateComment(testPost.getId(), testRootComment.getId(), request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Comment deleted with ID: " + testRootComment.getId());

            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("Delete Comment Method")
    class DeleteCommentTests {

        @Test
        @DisplayName("Should hard delete comment when is has no replies")
        void deleteCommentShouldHardDeleteWhenNoRepliesExist() {
            //Arrange
            when(commentRepository.findByIdAndPostId(testRootComment.getId(), testPost.getId()))
                    .thenReturn(Optional.of(testRootComment));

            //Act
            commentService.deleteComment(testPost.getId(), testRootComment.getId());

            //Assert
            verify(commentRepository, times(1)).delete(testRootComment);
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("Should soft delete comment (change status and content) when it has replise")
        void deleteCommentShouldSoftDeleteWhenRepliesExist() {
            //Arrange
            when(commentRepository.findByIdAndPostId(testParentCommentWithReplies.getId(), testPost.getId()))
                    .thenReturn(Optional.of(testParentCommentWithReplies));
            when(commentRepository.save(any(Comment.class))).thenReturn(testParentCommentWithReplies);

            //Act
            commentService.deleteComment(testPost.getId(), testParentCommentWithReplies.getId());

            //Assert
            ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
            verify(commentRepository, times(1)).save(commentCaptor.capture());

            Comment savedComment = commentCaptor.getValue();
            assertThat(savedComment.getStatus()).isEqualTo(CommentStatus.DELETED);
            assertThat(savedComment.getContent()).isEqualTo("[Comment is deleted]");
            verify(commentRepository, never()).delete(any(Comment.class));
        }

    }
}