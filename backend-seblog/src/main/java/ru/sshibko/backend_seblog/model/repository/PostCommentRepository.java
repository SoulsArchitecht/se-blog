package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.PostComment;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
