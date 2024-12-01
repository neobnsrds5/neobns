package com.example.neobns.config;

import java.util.Map;

import org.apache.hc.core5.http.nio.NHttpMessageWriter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedissonCacheconfig {

	@Value("${spring.cache.redis.ttl:60000}")
	private long cacheTtl;

	@Bean
	public CacheManager cacheManager(RedissonClient redissonClient) {

		RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redissonClient);
		cacheManager.setCodec(new JsonJacksonCodec());
		return cacheManager;

	}

}
