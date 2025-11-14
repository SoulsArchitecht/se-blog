package ru.sshibko.backend_seblog.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sshibko.backend_seblog.model.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
