package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ViewLogService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_KEY_PREFIX = "post::view";

    @Value("${redis.ttl-hours}")
    private static final int TTL_HOURS = 24;

    public boolean hasUserViewedToday(UUID postId, UUID userId) {
        String key = getViewKey(postId);
        String number = formatMember(userId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, number));
    }

    public void logView(UUID postId, UUID userId) {
        String key = getViewKey(postId);
        String member = formatMember(userId);

        redisTemplate.opsForSet().add(key, member);
        redisTemplate.expire(key, TTL_HOURS, TimeUnit.HOURS);
    }

    private String getViewKey(UUID postId) {
        return VIEW_KEY_PREFIX + postId;
    }

    private String formatMember(UUID userId) {
        return userId.toString();
    }
}
