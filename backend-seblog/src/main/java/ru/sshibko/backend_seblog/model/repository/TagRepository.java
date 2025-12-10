package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.Tag;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Set<Tag> findByNameIn(Set<String> names);

    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT t FROM Tag t LEFT JOIN t.posts p " +
            "WHERE p.status = 'PUBLISHED' " +
            "GROUP BY t.id " +
            "ORDER BY COUNT(p.id) DESC")
    Page<Tag> findPopularTags(Pageable pageable);

    @Query("SELECT t, COUNT(p) as postCount FROM Tag t " +
            "LEFT JOIN t.posts p " +
            "WHERE p.status = 'PUBLISHED' OR p IS NULL " +
            "GROUP BY t.id")
    Page<Object[]> findTagsWithPostCount(Pageable pageable);
}
