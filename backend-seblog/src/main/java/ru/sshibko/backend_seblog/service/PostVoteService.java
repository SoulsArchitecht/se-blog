package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sshibko.backend_seblog.dto.request.VoteRequest;
import ru.sshibko.backend_seblog.exception.ResourceNotFoundException;
import ru.sshibko.backend_seblog.exception.ValidationException;
import ru.sshibko.backend_seblog.model.entity.Post;
import ru.sshibko.backend_seblog.model.entity.PostVote;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.VoteType;
import ru.sshibko.backend_seblog.model.repository.PostRepository;
import ru.sshibko.backend_seblog.model.repository.PostVoteRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional
public class PostVoteService {

    private final PostVoteRepository postVoteRepository;

    private final PostRepository postRepository;

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR','ROLE_ADMIN')")
    @CacheEvict(value = {"postVoteStatus", "postVoteCounts"}, key = "#postId")
    public void voteForPost(UUID postId, VoteRequest request){
        log.info("voteForPost {}: {}", postId, request.type());

        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: "  + postId));


        if (post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ValidationException("You can't vote for your own post");
        }

        Optional<PostVote> existingVote = postVoteRepository.findByUserIdAndPostId(
                currentUser.getId(), postId);

        if (existingVote.isPresent()) {
            PostVote postVote = existingVote.get();
            if (postVote.getType() == request.type()) {
                postVoteRepository.delete(postVote);
                log.info("voteForPost {}: {} deleted", postId, request.type());
            } else {
                postVote.setType(request.type());
                postVoteRepository.save(postVote);
                log.info("voteForPost {}: {} updated", postId, request.type());
            }
        } else {
            PostVote postVote = PostVote.builder()
                    .type(request.type())
                    .user(currentUser)
                    .post(post)
                    .build();

            postVoteRepository.save(postVote);
            log.info("voteForPost {}: {} created", postId, request.type());
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "postVoteStats", key = "#postId")
    public VoteType getCurrentUserVoteForPost(UUID postId) {
        User currentUser = userService.getCurrentUserOrNull();

        if (currentUser == null) {
            return null;
        }

        return postVoteRepository.findByUserIdAndPostId(currentUser.getId(), postId)
                .map(PostVote::getType)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "postVoteCounts", key = "'likes:' + #postId")
    public Integer getPostLikeCount(UUID postId) {
        return postVoteRepository.countByPostIdAndType(postId, VoteType.LIKE);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "postVoteCounts", key = "'dislikes:' + #postId")
    public Integer getPostDislikeCount(UUID postId) {
        return postVoteRepository.countByPostIdAndType(postId, VoteType.DISLIKE);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @CacheEvict(value = {"postVoteStats", "postVoteCounts"}, key = "#postId")
    public void removePostVote(UUID postId) {
        User currentUser = userService.getCurrentUser();

        PostVote postVote = postVoteRepository.findByUserIdAndPostId(currentUser.getId(), postId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found"));

        postVoteRepository.delete(postVote);
        log.info("Vote removed for post {}, user: {}", postId, currentUser.getId());
    }
}
