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
import org.springframework.web.multipart.MultipartFile;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.dto.UserPublicProfileDto;
import ru.sshibko.backend_seblog.mapper.UserProfileMapperService;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;
import ru.sshibko.backend_seblog.model.repository.CommentRepository;
import ru.sshibko.backend_seblog.model.repository.PostRepository;
import ru.sshibko.backend_seblog.model.repository.UserProfileRepository;
import ru.sshibko.backend_seblog.model.repository.UserRepository;
import ru.sshibko.backend_seblog.testdata.UserProfileTestData;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for UserProfileService")
class UserProfileServiceTest {

    @Mock private UserProfileRepository userProfileRepository;
    @Mock private UserService userService;
    @Mock private UserProfileMapperService userProfileMapper;
    @Mock private FileStorageService fileStorageService;
    @Mock private UserRepository userRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private User testUser;
    private UserProfile testUserProfile;
    private UserProfileDto mockUserProfileDto;

    @BeforeEach
    void setUp() {
        testUser = UserProfileTestData.createUser();
        testUserProfile = UserProfileTestData.createProfile(testUser);
        mockUserProfileDto = mock(UserProfileDto.class);
    }

    @Nested
    @DisplayName("Update User Profile Method")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should partially update ONLY provided fields and preserve others")
        void updateCurrentUserProfileShouldPartiallyUpdateWhenSomeFieldsAreNull() {
            //Arrange
            UserProfileDto updateRequest = UserProfileTestData.createUpdateUserProfileDto(
                    "New Bio", "New Location");

            when(userService.getCurrentUser()).thenReturn(testUser);
            when(userProfileRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testUserProfile));
            when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testUserProfile);
            when(userProfileMapper.mapToDto(any(UserProfile.class))).thenReturn(mockUserProfileDto);

            //Act
            userProfileService.updateCurrentUserProfile(updateRequest);

            //Assert
            ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
            verify(userProfileRepository, times(1)).save(profileCaptor.capture());

            UserProfile savedProfile = profileCaptor.getValue();

            assertThat(savedProfile.getBio()).isEqualTo("New Bio");
            assertThat(savedProfile.getLocation()).isEqualTo("New Location");

            assertThat(savedProfile.getDisplayName()).isEqualTo("John Lennon");
        }

        @Test
        @DisplayName("Should create default profile if it does not exist before updating")
        void updateCurrentUserProfileShouldCreateDefaultProfileWhenNotFound() {
            //Arrange
            UserProfileDto updateRequest = UserProfileTestData.createUpdateUserProfileDto(
                    "New Bio", null
            );

            when(userService.getCurrentUser()).thenReturn(testUser);
            when(userProfileRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
            when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(
                    invocation -> invocation.getArgument(0) );
            when(userProfileMapper.mapToDto(any(UserProfile.class))).thenReturn(mockUserProfileDto);

            //Act
            userProfileService.updateCurrentUserProfile(updateRequest);

            //Assert
            ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
            verify(userProfileRepository, times(2)).save(profileCaptor.capture());

            List<UserProfile> allSavedProfiles = profileCaptor.getAllValues();

            UserProfile initiallySaved = allSavedProfiles.get(0);
            assertThat(initiallySaved.getUser()).isEqualTo(testUser);
            assertThat(initiallySaved.getBio()).isEqualTo("New Bio");

            UserProfile finallySaved = allSavedProfiles.get(1);
            assertThat(finallySaved.getUser()).isEqualTo(testUser);
            assertThat(finallySaved.getBio()).isEqualTo("New Bio");
        }
    }

    @Nested
    @DisplayName("Public Profile Method")
    class PublicProfileTests {

        @Test
        @DisplayName("Should aggregate user, profile, post count and comment count correctly")
        void getUserPublicProfileShouldAggregateDataCorrectly() {
            //Arrange
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
            when(userProfileRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testUserProfile));
            when(postRepository.countPublishedPostsByAuthor(testUser.getId())).thenReturn(42L);
            when(commentRepository.countVisibleCommentsByAuthor(testUser.getId())).thenReturn(15L);

            //Act
            UserPublicProfileDto result = userProfileService.getUserPublicProfile(testUser.getId());

            //Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("author");
            assertThat(result.getDisplayName()).isEqualTo("John Lennon");
            assertThat(result.getPostCount()).isEqualTo(42L);
            assertThat(result.getCommentCount()).isEqualTo(15L);

            verify(postRepository, times(1)).countPublishedPostsByAuthor(testUser.getId());
            verify(commentRepository, times(1)).countVisibleCommentsByAuthor(testUser.getId());
        }
    }

    @Nested
    @DisplayName("Avatar Upload Method")
    class AvatarUploadTests {

        @Test
        @DisplayName("Should store file and update avatar URL")
        void uploadAvatarShouldStoreFileAndUpdateUrl() {
            //Arrange
            MultipartFile mockFile = mock(MultipartFile.class);
            String storedFileName = "avatar-123.jpg";

            when(userService.getCurrentUser()).thenReturn(testUser);
            when(userProfileRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testUserProfile));
            when(fileStorageService.storeAvatar(mockFile)).thenReturn(storedFileName);
            when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testUserProfile);

            //Act
            String resultUrl = userProfileService.uploadAvatar(mockFile);

            //Assert
            assertThat(resultUrl).isEqualTo(storedFileName);
            verify(fileStorageService, times(1)).storeAvatar(mockFile);

            ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
            verify(userProfileRepository, times(1)).save(profileCaptor.capture());
            assertThat(profileCaptor.getValue().getAvatarUrl()).isEqualTo(storedFileName);
        }
    }
}