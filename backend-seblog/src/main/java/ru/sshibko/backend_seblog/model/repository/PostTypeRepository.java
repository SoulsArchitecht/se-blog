package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.PostType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostTypeRepository extends JpaRepository<PostType, UUID> {

    Optional<PostType> findPostTypeBySlug(String typeSlug);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    Optional<PostType> findByName(String name);

    boolean existsById(UUID id);
}
