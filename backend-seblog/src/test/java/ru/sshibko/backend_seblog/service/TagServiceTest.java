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
import ru.sshibko.backend_seblog.dto.request.TagCreateRequest;
import ru.sshibko.backend_seblog.dto.response.TagResponse;
import ru.sshibko.backend_seblog.exception.NotFoundException;
import ru.sshibko.backend_seblog.exception.ValidationException;
import ru.sshibko.backend_seblog.mapper.TagMapperService;
import ru.sshibko.backend_seblog.model.entity.Tag;
import ru.sshibko.backend_seblog.model.repository.TagRepository;
import ru.sshibko.backend_seblog.testdata.TagTestData;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for TagService")
class TagServiceTest {

    @Mock private TagRepository tagRepository;
    @Mock private TagMapperService tagMapper;
    @Mock private SlugService slugService;

    @InjectMocks
    private TagService tagService;

    private Tag testTag;
    private TagResponse mockTagResponse;

    @BeforeEach
    void setTup() {
        testTag = TagTestData.createTag("java");
        mockTagResponse = mock(TagResponse.class);
    }

    @Nested
    @DisplayName("Get Tag By ID Method")
    class GetTagIdTests {

        @Test
        @DisplayName("Should return tag response when tag exists")
        void getTagByIdShouldReturnResponseWhenTagExists() {
            //Arrange
            UUID tagId = testTag.getId();
            when(tagRepository.findById(tagId)).thenReturn(java.util.Optional.of(testTag));
            when(tagMapper.mapToResponse(testTag)).thenReturn(mockTagResponse);

            //Act
            TagResponse result = tagService.getTagById(tagId);

            //Assert
            assertThat(result).isEqualTo(mockTagResponse);
            verify(tagRepository, times(1)).findById(tagId);
        }

        @Test
        @DisplayName("Should throw NotFoundException when tag does not exist")
        void getTagByIdShouldThrowExceptionWhenTagNotFound() {
            //Arrange
            UUID tagId = UUID.randomUUID();
            when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> tagService.getTagById(tagId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Tag with identifier '" + tagId + "' not found");

            verify(tagMapper, never()).mapToResponse(any(Tag.class));
        }
    }

    @Nested
    @DisplayName("Create Tag Method")
    class CreateTagTests {

        @Test
        @DisplayName("Should create and return new tag when name is unique")
        void createTagShouldCreateTagWhenNameIsUnique() {
            //Arrange
            TagCreateRequest request = TagTestData.createTagRequest("Spring");
            Tag savedTag = TagTestData.createTag("Spring");

            when(tagRepository.existsByName("Spring")).thenReturn(false);
            when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);
            when(tagMapper.mapToResponse(savedTag)).thenReturn(mockTagResponse);

            //Act
            TagResponse result = tagService.createTag(request);

            //Assert
            assertThat(result).isEqualTo(mockTagResponse);

            ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
            verify(tagRepository, times(1)).save(tagCaptor.capture());
            assertThat(tagCaptor.getValue().getName()).isEqualTo("Spring");
        }

        @Test
        @DisplayName("Should throw ValidationException when tag name already exists")
        void createTagShouldThrowExceptionWhenTagAlreadyExists() {
            //Arrange
            TagCreateRequest request = TagTestData.createTagRequest("Java");
            when(tagRepository.existsByName("Java")).thenReturn(true);

            //Act & Assert
            assertThatThrownBy(() -> tagService.createTag(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Tag already exists with name " + request.name());
            verify(tagRepository, never()).save(any(Tag.class));
        }
    }

    @Nested
    @DisplayName("Get Or Create Tags Method (Complex Logic)")
    class GetOrCreateTagsTests {

        @Test
        @DisplayName("Should return empty set when is null or empty")
        void getOrCreateTagsShouldReturnEmptySetWhenInputIsNullOrEmpty() {
            //Act & Assert
            assertThat(tagService.getOrCreateTags(null)).isEmpty();
            assertThat(tagService.getOrCreateTags(Set.of())).isEmpty();

            verify(tagRepository, never()).findByNameIn(anySet());
            verify(tagRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Should return only existing tags when all found in DB")
        void getOrCreateTagsShouldReturnExistingTagsWhenAllExist() {
            //Arrange
            Set<String> requestNames = Set.of("java", "spring");
            Tag tag1 = TagTestData.createTag("java");
            Tag tag2 = TagTestData.createTag("spring");
            Set<Tag> existingTags = Set.of(tag1, tag2);

            when(tagRepository.findByNameIn(requestNames)).thenReturn(existingTags);

            //Act
            Set<Tag> result = tagService.getOrCreateTags(requestNames);

            //Assert
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(tag1, tag2);
            verify(tagRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Should create and return new tags when none exist in DB")
        void getOrCreateTagsShouldCreateNewTagsWhenNoneExist() {
            //Arrange
            Set<String> requestedNames = Set.of("rust", "go");
            when(tagRepository.findByNameIn(requestedNames)).thenReturn(Set.of());

            //Act
            Set<Tag> result = tagService.getOrCreateTags(requestedNames);

            //Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Tag::getName).containsExactlyInAnyOrder("rust", "go");

            ArgumentCaptor<Set<Tag>> savedTagsCaptor = ArgumentCaptor.forClass(Set.class);
            verify(tagRepository, times(1)).saveAll(savedTagsCaptor.capture());

            Set<Tag> savedTags = savedTagsCaptor.getValue();
            assertThat(savedTags).hasSize(2);
        }

        @Test
        @DisplayName("Should handle mixed scenario: return existing and save only new ones, ignoring blanks")
        void getOrCreateTagsShouldHandleMixedScenarioAndNormalizeInput() {
            Set<String> requestedNames = Set.of(" java ", "   ", "rust");

            Tag existingJava = TagTestData.createTag("java");
            when(tagRepository.findByNameIn(anySet())).thenReturn(Set.of(existingJava));

            // Act
            Set<Tag> result = tagService.getOrCreateTags(requestedNames);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Tag::getName).containsExactlyInAnyOrder("java", "rust");

            ArgumentCaptor<Set<Tag>> savedTagsCaptor = ArgumentCaptor.forClass(Set.class);
            verify(tagRepository, times(1)).saveAll(savedTagsCaptor.capture());

            Set<Tag> savedTags = savedTagsCaptor.getValue();
            assertThat(savedTags).hasSize(1);
            assertThat(savedTags.iterator().next().getName()).isEqualTo("rust");
        }
    }
}