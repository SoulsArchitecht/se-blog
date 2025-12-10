package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sshibko.backend_seblog.dto.TagCreateRequest;
import ru.sshibko.backend_seblog.dto.TagResponse;
import ru.sshibko.backend_seblog.dto.TagUpdateRequest;
import ru.sshibko.backend_seblog.exception.ResourceNotFoundException;
import ru.sshibko.backend_seblog.exception.ValidationException;
import ru.sshibko.backend_seblog.mapper.TagMapperService;
import ru.sshibko.backend_seblog.model.entity.Tag;
import ru.sshibko.backend_seblog.model.repository.TagRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagService {

    private final TagRepository tagRepo;

    private final SlugService slugService;

    private final TagMapperService tagMapper;

    @Transactional
    public TagResponse createTag(TagCreateRequest request) {
        String name = request.name().trim();

        if(tagRepo.existsByName(name)) {
            throw new ValidationException("Tag with name " + name + " already exists");
        }

        Tag tag = Tag.builder()
                .name(name)
                .build();

        Tag newTag = tagRepo.save(tag);
        log.info("Created tag with name {}", tag.getName());

        return tagMapper.mapToResponse(newTag);
    }

    @Transactional
    public Tag getOrCreateTag(String tagName) {
        String name = tagName.trim();

        return tagRepo.findByName(name)
                .orElseGet(() -> {
                    Tag newTag = Tag.builder()
                            .name(name)
                            .build();
                    return tagRepo.save(newTag);
                });
    }

    @Transactional
    public Set<Tag> getOrCreateTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> normalizedTagNames = tagNames.stream()
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toSet());

        if (normalizedTagNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<Tag> existingTags = tagRepo.findByNameIn(normalizedTagNames);
        Set<String> existingNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        Set<Tag> newTags = normalizedTagNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> Tag.builder().name(name).build())
                .collect(Collectors.toSet());

        if (newTags.isEmpty()) {
            tagRepo.saveAll(newTags);
        }

        Set<Tag> allTags = new HashSet<>(existingTags);
        allTags.addAll(newTags);

        return allTags;
    }

    @Transactional
    public TagResponse updateTag(UUID id, TagUpdateRequest request) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id " + id + " not found"));

        tagMapper.updateEntityFromRequest(tag, request);

        if (request.name() != null && !request.name().isBlank() ) {
            String newName = request.name().trim();
            if (!newName.equals(tag.getName())) {
                if (tagRepo.existsByNameAndIdNot(newName, id)) {
                    throw new ValidationException("Tag with name " + newName + " already exists");
                }
                tag.setName(newName);
            }
        }

        Tag newTag = tagRepo.save(tag);
        log.info("Updated tag with name {}", tag.getName());

        return tagMapper.mapToResponse(newTag);
    }

    @Transactional
    public void deleteTag(UUID id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id " + id + " not found"));

        if (!tag.getPosts().isEmpty()) {
            throw new ValidationException(
                    "Cannot delete tag with associated posts. Remove tag from posts first");
        }

        tagRepo.delete(tag);
        log.info("Deleted tag {} with id {}", tag.getName(), tag.getId());
    }

    @Transactional(readOnly = true)
    public TagResponse getTagById(UUID id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id " + id + " not found"));
        return tagMapper.mapToResponse(tag);
    }

/*    @Transactional
    public Set<TagResponse> getAllTags(Pageable pageable) {
        Page<Set<Tag>> allTags = tagRepo.findAll(pageable)
    }*/

    @Transactional(readOnly = true)
    public Page<TagResponse> searchTags(String query, Pageable pageable) {
        Page<Tag> tags;

        if (query != null && !query.isBlank()) {
            tags = tagRepo.findByNameContainingIgnoreCase(query, pageable);
        } else {
            tags = tagRepo.findAll(pageable);
        }

        return tags.map(tagMapper::mapToResponse);
    }

    @Transactional
    public Page<TagResponse> searchTags(Pageable pageable) {
        return tagRepo.findTagsWithPostCount(pageable)
                .map(result -> {
                    Tag tag = (Tag) result[0];
                    Long postCount = (Long) result[1];
                    return new TagResponse(
                            tag.getId(),
                            tag.getName(),
                            slugService.generateSlug(tag.getName()),
                            tag.getCreatedAt(),
                            postCount.intValue()
                    );
                });
    }
}
