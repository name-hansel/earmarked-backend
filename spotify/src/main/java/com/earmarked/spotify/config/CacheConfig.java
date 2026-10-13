package com.earmarked.spotify.config;

import com.earmarked.spotify.dto.Album;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.CollectionType;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String ALBUMS = "albums";
    public static final String ALBUM_SEARCH = "albumSearch";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, JsonMapper jsonMapper) {
        JacksonJsonRedisSerializer<Album> albumSerializer = new JacksonJsonRedisSerializer<>(jsonMapper,
                Album.class);

        CollectionType searchType = jsonMapper.getTypeFactory().constructCollectionType(List.class, Album.class);
        JacksonJsonRedisSerializer<Object> searchSerializer = new JacksonJsonRedisSerializer<>(
                jsonMapper, searchType);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration(ALBUMS,
                        redisCacheConfiguration
                                .entryTtl(Duration.ofHours(24))
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(albumSerializer)))
                .withCacheConfiguration(ALBUM_SEARCH,
                        redisCacheConfiguration
                                .entryTtl(Duration.ofHours(1))
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(searchSerializer)))
                .build();
    }
}
