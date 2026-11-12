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
import ru.sshibko.backend_seblog.dto.request.PostCreateRequest;
import ru.sshibko.backend_seblog.dto.request.PostUpdateRequest;
import ru.sshibko.backend_seblog.dto.response.PostResponse;
import ru.sshibko.backend_seblog.dto.security.HtmlSanitizer;
import ru.sshibko.backend_seblog.exception.AccessDeniedException;
import ru.sshibko.backend_seblog.exception.NotFoundException;
import ru.sshibko.backend_seblog.exception.ValidationException;
import ru.sshibko.backend_seblog.mapper.PostMapperService;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.PostType;
import ru.sshibko.backend_seblog.model.entity.Tag;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.repository.PostRepository;
import ru.sshibko.backend_seblog.model.repository.PostTypeRepository;
import ru.sshibko.backend_seblog.testdata.PostTestData;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for PostService")
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private PostMapperService postMapper;
    @Mock private PostTypeRepository postTypeRepository;
    @Mock private TagService tagService;
    @Mock private UserService userService;
    @Mock private SlugService slugService;
    @Mock private HtmlSanitizer htmlSanitizer;

    @Mock private ViewLogService viewLogService;
    @Mock private CommentService commentService;
    @Mock private PostTypeService postTypeService;

    @InjectMocks
    private PostService postService;

    private User testAuthor;
    private PostType testPostType;
    private Post testPost;
    private PostResponse mockPostResponse;

    @BeforeEach
    void setUp() {
        testAuthor = PostTestData.createAuthor();
        testPostType = PostTestData.createPostType("Hardware");
        testPost = PostTestData.createPost(testAuthor, testPostType);
        mockPostResponse = mock(PostResponse.class);

        //when(userService.getCurrentUser()).thenReturn(testAuthor);
        //when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
    }

    @Nested
    @DisplayName("Create Post Method")
    class CreatePostTests {

        @Test
        @DisplayName("Should create post with auto-generated slug and sanitized content")
        void createPostShouldCreatePostWithAutoSlugWhenCustomSlugIsNull() {
            //Arrange
            PostCreateRequest request = PostTestData.createPostCreateRequest(
                    "My New Post", null, "Hardware");
            String  generatedSlug = "my-new-post-123";
            String sanitizedContent = "<p>Sanitized Content</p>";
            Set<Tag> mockTags = Set.of(mock(Tag.class));

            when(userService.getCurrentUser()).thenReturn(testAuthor);
            when(postTypeRepository.findByName("Hardware")).thenReturn(Optional.of(testPostType));
            when(slugService.generateUniqueSlug(eq("My New Post"), any())).thenReturn(generatedSlug);
            when(htmlSanitizer.sanitize("<p>Test content</p>")).thenReturn(sanitizedContent);
            when(tagService.getOrCreateTags(Set.of("tag1", "tag2"))).thenReturn(mockTags);
            when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
                Post saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });
            when(postMapper.mapToResponse(any(Post.class))).thenReturn(mockPostResponse);

            //Act
            PostResponse response = postService.createPost(request);

            //Assert
            assertThat(response).isEqualTo(mockPostResponse);

            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
            verify(postRepository, times(1)).save(postCaptor.capture());

            Post savedPost = postCaptor.getValue();
            assertThat(savedPost.getTitle()).isEqualTo("My New Post");
            assertThat(savedPost.getSlug()).isEqualTo(generatedSlug);
            assertThat(savedPost.getContent()).isEqualTo(sanitizedContent);
            assertThat(savedPost.getAuthor()).isEqualTo(testAuthor);
            assertThat(savedPost.getType()).isEqualTo(testPostType);
        }

        @Test
        @DisplayName("Should throw ValidationException when custom slug already exists")
        void createPostShouldThrowExceptionWhenCustomSlugExists() {
            //Arrange
            PostCreateRequest request = PostTestData.createPostCreateRequest(
                    "Title", "existing-slug", "Hardware");
            when(userService.getCurrentUser()).thenReturn(testAuthor);
            when(postTypeRepository.findByName("Hardware")).thenReturn(Optional.of(testPostType));
            when(slugService.generateSlug("existing-slug")).thenReturn("existing-slug");
            when(slugService.isValidSlug("existing-slug")).thenReturn(true);
            when(postRepository.existsBySlug("existing-slug")).thenReturn(true);

            //Act & Assert
            assertThatThrownBy(() -> postService.createPost(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Post already exists with Slug existing-slug");

            verify(postRepository, never()).save(any(Post.class));
        }

        @Test
        @DisplayName("Should throw NotFoundException when post type does not exist")
        void createPostShouldThrowExceptionWhenPostTypeNotFound() {
            // Arrange
            PostCreateRequest request = PostTestData.createPostCreateRequest(
                    "Title", null, "UnknownType");

            when(userService.getCurrentUser()).thenReturn(testAuthor);
            when(postTypeRepository.findByName("UnknownType")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> postService.createPost(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(
                            "PostType with identifier 'UnknownType' not found");
        }
    }

    @Nested
    @DisplayName("Update Post Method")
    class UpdatePostTests {

        @Test
        @DisplayName("Should update ONLY provided fields and preserve old slug, type and tags")
        void updatePostShouldPreserveOldValuesWhenNotProvidedInRequest() {
            //Arrange
            PostUpdateRequest request = PostTestData.createPostUpdateRequest(
                    "Updated Title", "<p>Updated</p>", null, null);
            String sanitizedContent = "<p>Sanitized Updated</p>";

            when(userService.getCurrentUser()).thenReturn(testAuthor);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));

            when(htmlSanitizer.sanitize("<p>Updated</p>")).thenReturn(sanitizedContent);
            when(postRepository.save(any(Post.class))).thenReturn(testPost);
            when(postMapper.mapToResponse(any(Post.class))).thenReturn(mockPostResponse);

            //Act
            postService.updatePost(testPost.getId(), request);

            //Assert
            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
            verify(postRepository, times(1)).save(postCaptor.capture());

            Post updatedPost = postCaptor.getValue();

            assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
            assertThat(updatedPost.getContent()).isEqualTo(sanitizedContent);

            assertThat(updatedPost.getSlug()).isEqualTo("original-title");
            assertThat(updatedPost.getType()).isEqualTo(testPostType);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user is not the author")
        void updatePostShouldThrowAccessDeniedExceptionWhenUserIsNotAuthor() {
            //Arrange
            User somebody = PostTestData.createAnotherUser();
            PostUpdateRequest request = PostTestData.createPostUpdateRequest(
                    "Hacked", null , null, null);
            when(userService.getCurrentUser()).thenReturn(somebody);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));

            //Act & Assert
            assertThatThrownBy(() -> postService.updatePost(testPost.getId(), request))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("User " + somebody.getId() +
                            " cannot edit post " + testPost.getId());

            verify(postRepository, never()).save(any(Post.class));
        }

        @Test
        @DisplayName("Should update slug successfully when valid and unique")
        void updatePostShouldUpdateSlugWhenValidAndUnique() {
            // Arrange
            PostUpdateRequest request = PostTestData.createPostUpdateRequest(
                    null, null, "new-valid-slug", null);

            when(userService.getCurrentUser()).thenReturn(testAuthor);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(slugService.isValidSlug("new-valid-slug")).thenReturn(true);
            when(postRepository.existsBySlugAndIdNot("new-valid-slug", testPost.getId())).thenReturn(false);
            when(postRepository.save(any(Post.class))).thenReturn(testPost);
            when(postMapper.mapToResponse(any(Post.class))).thenReturn(mockPostResponse);

            // Act
            postService.updatePost(testPost.getId(), request);

            // Assert
            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
            verify(postRepository, times(1)).save(postCaptor.capture());

            assertThat(postCaptor.getValue().getSlug()).isEqualTo("new-valid-slug");
        }

        @Test
        @DisplayName("Should throw ValidationException when new slug is invalid")
        void updatePostShouldThrowExceptionWhenNewSlugIsInvalid() {
            // Arrange
            PostUpdateRequest request = PostTestData.createPostUpdateRequest(
                    null, null, "Invalid Slug With Spaces!", null);

            when(userService.getCurrentUser()).thenReturn(testAuthor);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(slugService.isValidSlug("Invalid Slug With Spaces!")).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> postService.updatePost(testPost.getId(), request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Incorrect format of slug");

            verify(postRepository, never()).save(any(Post.class));
        }

        @Test
        @DisplayName("Should update post type when new postTypeName is provided")
        void updatePostShouldUpdatePostTypeWhenNewTypeNameProvided() {
            // Arrange
            PostType newType = PostTestData.createPostType("Software");
            PostUpdateRequest request = PostTestData.createPostUpdateRequest(
                    null, null, null, "Software");

            when(userService.getCurrentUser()).thenReturn(testAuthor);
            when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));
            when(postTypeRepository.findByName("Software")).thenReturn(Optional.of(newType));
            when(postRepository.save(any(Post.class))).thenReturn(testPost);
            when(postMapper.mapToResponse(any(Post.class))).thenReturn(mockPostResponse);

            // Act
            postService.updatePost(testPost.getId(), request);

            // Assert
            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
            verify(postRepository, times(1)).save(postCaptor.capture());

            assertThat(postCaptor.getValue().getType()).isEqualTo(newType);
        }
    }

    @Nested
    @DisplayName("Get Post Method")
    class GetPostTests {

        @Test
        @DisplayName("Should increment view count when getting post by slug")
        void getPostBySlugShouldIncrementViewCount() {
            //Arrange
            when(postRepository.findBySlugWithDetails("original-title")).thenReturn(Optional.of(testPost));
            when(postMapper.mapToResponse(any(Post.class))).thenReturn(mockPostResponse);

            //Act
            postService.getPostBySlug("original-title");

            //Assert
            verify(postRepository, times(1)).incrementViewCount(testPost.getId());
            verify(postMapper, times(1)).mapToResponse(testPost);
        }
    }
}