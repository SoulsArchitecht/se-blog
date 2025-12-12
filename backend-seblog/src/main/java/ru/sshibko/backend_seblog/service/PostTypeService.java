package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sshibko.backend_seblog.dto.response.PostTypeResponse;
import ru.sshibko.backend_seblog.exception.ResourceNotFoundException;
import ru.sshibko.backend_seblog.mapper.PostTypeMapperService;
import ru.sshibko.backend_seblog.model.entity.PostType;
import ru.sshibko.backend_seblog.model.repository.PostTypeRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CacheConfig(cacheNames = "postTypes")
@Transactional(readOnly = true)
public class PostTypeService {

    private final PostTypeRepository postTypeRepository;

    private final PostTypeMapperService postMapper;

    @Cacheable(key = "'all'")
    public List<PostTypeResponse> getAllPostTypes() {
        log.debug("getAllPostTypes()");
        return postTypeRepository.findAll().stream()
                .map(postMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "#id")
    public PostTypeResponse getPostTypeById(UUID id) {
        log.debug("getPostTypeById({})", id);
        PostType postType = postTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PostType with id: " + id + " not found!") );
        return postMapper.mapToResponse(postType);
    }

    public boolean existsById(UUID id) {
        log.debug("existsById({})", id);
        return postTypeRepository.existsById(id);
    }
}
