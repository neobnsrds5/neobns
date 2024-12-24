package com.example.neobns.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private String redisPort;

	@Value("${spring.data.redis.password}")
	private String redisPassword;

	@Bean
	public RedissonClient redissonClient() {

		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort)
				.setPassword(redisPassword.isEmpty() ? null : redisPassword);

		return Redisson.create(config);

	}

}