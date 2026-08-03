package ru.sshibko.backend_seblog.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {


    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY // Добавляет поле "@class"
        );

        return mapper;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper redisObjectMapper) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .withCacheConfiguration("posts", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("postTypes", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1)))
                .withCacheConfiguration("tags", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(12)))
                .withCacheConfiguration("postVoteStats", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("commentVoteStats", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("postVoteCounts", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("commentVoteCounts", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("comments", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("users", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("slugs", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(12)))
                .build();
    }

   /* *
    private ObjectMapper createRedisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрируем модуль для работы с Java 8 датами
        mapper.registerModule(new JavaTimeModule());

        // Сериализуем даты как ISO-строки, а не как timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

*//*        // Для GenericJackson2JsonRedisSerializer нужно сохранять информацию о типах
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );*//*

        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Используем кастомный ObjectMapper
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(createRedisObjectMapper());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Создаём единый сериализатор с поддержкой Java 8 дат
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(createRedisObjectMapper());

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));  // Используем кастомный сериализатор

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .withCacheConfiguration("posts",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("postTypes",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1)))
                .withCacheConfiguration("tags",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(12)))
                .withCacheConfiguration("postVoteStats",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("commentVoteStats",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("postVoteCounts",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("commentVoteCounts",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("comments",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("users",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("slugs",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(12)))
                .build();
    }*/
}