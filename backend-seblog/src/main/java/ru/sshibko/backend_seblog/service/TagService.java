package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sshibko.backend_seblog.dto.TagCreateRequest;
import ru.sshibko.backend_seblog.dto.response.TagResponse;
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

    private final TagRepository tagRepository;

    private final SlugService slugService;

    private final TagMapperService tagMapper;

    @Transactional(readOnly = true)
    public Page<TagResponse> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(tagMapper::mapToResponse);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "#id")
    public TagResponse getTagById(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
        return tagMapper.mapToResponse(tag);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @CacheEvict(value = "tags", allEntries = true)
    public TagResponse createTag(TagCreateRequest request) {
        String name = request.name().trim();

        if (tagRepository.existsByName(name)) {
            throw new ValidationException("Tag already exists: " + name);
        }

        Tag tag = Tag.builder()
                .name(name)
                .build();

        Tag savedTag = tagRepository.save(tag);
        log.info("Created tag: {}", savedTag.getName());

        return tagMapper.mapToResponse(savedTag);
    }

    @Transactional
    public Set<Tag> getOrCreateTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> normalizedNames = tagNames.stream()
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toSet());

        Set<Tag> existingTags = tagRepository.findByNameIn(normalizedNames);
        Set<String> existingNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        Set<Tag> newTags = normalizedNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> Tag.builder().name(name).build())
                .collect(Collectors.toSet());

        if (!newTags.isEmpty()) {
            tagRepository.saveAll(newTags);
        }

        Set<Tag> allTags = new HashSet<>(existingTags);
        allTags.addAll(newTags);

        return allTags;
    }
}
