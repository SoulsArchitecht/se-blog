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
import ru.sshibko.backend_seblog.dto.request.CommentCreateRequest;
import ru.sshibko.backend_seblog.dto.response.CommentResponse;
import ru.sshibko.backend_seblog.exception.ErrorCode;
import ru.sshibko.backend_seblog.exception.NotFoundException;
import ru.sshibko.backend_seblog.exception.ResourceNotFoundException;
import ru.sshibko.backend_seblog.exception.ValidationException;
import ru.sshibko.backend_seblog.mapper.CommentMapperService;
import ru.sshibko.backend_seblog.model.entity.Comment;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.CommentStatus;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.repository.CommentRepository;
import ru.sshibko.backend_seblog.model.repository.PostRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserService userService;

    private final CommentVoteService commentVoteService;

    private final CommentMapperService commentMapper;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @CacheEvict(value = {"commentsByPost", "commentsTree"}, key = "#postId")
    public CommentResponse createComment(UUID postId, CommentCreateRequest request) {
        log.info("Create comment for post: {}", postId);

        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.POST_NOT_FOUND,
                        "Post",
                        postId,
                        "Post with not found with ID: " + postId)
                );

        Comment parent = null;
        if (request.parentId() != null) {
            parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.COMMENT_NOT_FOUND,
                            "Comment",
                            request.parentId(),
                            "Parent comment not found with ID: " + request.parentId())
                    );

            if (!parent.getPost().getId().equals(post.getId())) {
                throw  new ValidationException(
                        ErrorCode.COMMENT_PARENT_MISMATCH,
                        "Parent comment with id: " + request.parentId() + " belongs to different post",
                        String.format("Parent comment with id: %s belongs to post %s, not %s",
                                parent.getId(), parent.getPost().getId(), post.getId())
                        );
            }
        }

        Comment comment = Comment.builder()
                .content(request.content())
                .author(currentUser)
                .post(post)
                .parent(parent)
                //.isDeleted(false) //TODO check in tests
                .status(CommentStatus.ACTIVE)
                .build();

        Comment newComment = commentRepository.save(comment);
        log.info("Comment created: ID {}", newComment.getId());

        return commentMapper.mapToResponse(comment);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "commentsByPost",
            key = "#postId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CommentResponse> getCommentsByPost(UUID postId, Pageable pageable) {
        log.debug("Get comments for post: {}: page {}, size {}",
                postId, pageable.getPageNumber(), pageable.getPageSize());

        return commentRepository.findAllByPostIdAndStatus(postId, CommentStatus.ACTIVE, pageable)
                .map(commentMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "commentTree", key = "#postId")
    public List<CommentResponse> getCommentTree(UUID postId) {
        log.debug("Get comments for post: {}", postId);

        List<Comment> rootComments = commentRepository.findRootCommentsByPostId(
                postId, CommentStatus.ACTIVE);

        return rootComments.stream()
                .map(this::buildCommentTree)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.COMMENT_NOT_FOUND,
                        "Comment",
                        commentId,
                        "Comment not found with ID: " + commentId));

        return commentMapper.mapToResponse(comment);
    }

    @PreAuthorize("@commentService.canEditComment(#commentId)")
    @CacheEvict(value = {"commentsByPost", "commentTree"}, key = "#postId")
    public CommentResponse updateComment(UUID postId, UUID commentId, CommentCreateRequest request) {
        log.info("Update comment for post: {}, commentId: {}", postId, commentId);

        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.COMMENT_NOT_FOUND,
                        "Comment",
                        commentId,
                        "Comment not found with ID: " + commentId)
                );

/*        if (comment.getIsDeleted()) {
            throw  new ValidationException("Comment with id: " + commentId + " is deleted");
        }*/
        if (comment.getStatus().equals(CommentStatus.DELETED)) {
            throw  new ValidationException(
                    ErrorCode.CANNOT_EDIT_DELETED_COMMENT,
                    "Comment is deleted",
                    "Comment deleted with ID: " + commentId);
        }

        comment.setContent(request.content());
        Comment updatedComment = commentRepository.save(comment);

        log.info("Comment updated: ID {}", updatedComment.getId());
        return commentMapper.mapToResponse(updatedComment);
    }

    @PreAuthorize("@commentService.canDeleteComment(#commentId)")
    @CacheEvict(value = {"commentsByPost", "commentTree"}, key = "#postId")
    public void deleteComment(UUID postId, UUID commentId) {
        log.info("Удаление комментария: {}", commentId);

        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.COMMENT_NOT_FOUND,
                        "Comment",
                        commentId,
                        "Comment not found with ID: " + commentId)
                );
        //if child comment exists marked as deleted
        if (!comment.getReplies().isEmpty()) {
            //comment.setIsDeleted(true); //TODO setIsDeleted()
            comment.setStatus(CommentStatus.DELETED);
            comment.setContent("[Comment is deleted]");
            commentRepository.save(comment);
            log.info("Comment is marked as deleted: ID {}", commentId);
        } else {
            commentRepository.delete(comment);
            log.info("Comment deleted: ID {}", commentId);
        }
    }

    public boolean canEditComment(UUID commentId) {
        User currentUser = userService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.COMMENT_NOT_FOUND,
                        "Comment",
                        commentId,
                        "Comment not found with ID: " + commentId)
                );

        if (currentUser.hasRole(UserRole.ROLE_ADMIN) || currentUser.hasRole(UserRole.ROLE_MODERATOR)) {
            return true;
        }

        return comment.getAuthor().getId().equals(currentUser.getId());
    }

    public boolean canDeleteComment(UUID commentId) {
        User currentUser = userService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.COMMENT_NOT_FOUND,
                        "Comment",
                        commentId,
                        "Comment not found with ID: " + commentId)
                );

        if (currentUser.hasRole(UserRole.ROLE_ADMIN)) {
            return true;
        }

        if (currentUser.hasRole(UserRole.ROLE_MODERATOR)) {
            return true;
        }

        return comment.getAuthor().getId().equals(currentUser.getId());
    }

    private CommentResponse buildCommentTree(Comment comment) {
        List<CommentResponse> replies = comment.getReplies().stream()
                //.filter(reply -> !reply.getIsDeleted()) //TODO isDeleted
                .filter(reply -> reply.getStatus() != CommentStatus.DELETED)
                .map(this::buildCommentTree)
                .toList();

        return commentMapper.mapToResponse(comment);
    }
}
