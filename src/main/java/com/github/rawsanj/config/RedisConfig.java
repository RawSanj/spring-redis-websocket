package com.github.rawsanj.config;

import com.github.rawsanj.messaging.RedisChatMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import static com.github.rawsanj.config.ChatConstants.ACTIVE_USER_KEY;
import static com.github.rawsanj.config.ChatConstants.MESSAGE_COUNTER_KEY;

@Slf4j
@Configuration(proxyBeanMethods=false)
@Profile("!heroku")
public class RedisConfig {

	@Bean
	ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisProperties redisProperties) {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
		redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	ReactiveStringRedisTemplate template(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
	}

	// Redis Atomic Counter to store no. of total messages sent from multiple app instances.
	@Bean
	RedisAtomicInteger chatMessageCounter(RedisConnectionFactory redisConnectionFactory) {
		return new RedisAtomicInteger(MESSAGE_COUNTER_KEY, redisConnectionFactory);
	}

	// Redis Atomic Counter to store no. of Active Users.
	@Bean
	RedisAtomicLong activeUserCounter(RedisConnectionFactory redisConnectionFactory) {
		return new RedisAtomicLong(ACTIVE_USER_KEY, redisConnectionFactory);
	}

	@Bean
	ApplicationRunner applicationRunner(RedisChatMessageListener redisChatMessageListener) {
		return args -> {
			redisChatMessageListener.subscribeMessageChannelAndPublishOnWebSocket()
				.doOnSubscribe(subscription -> log.info("Redis Listener Started"))
				.doOnError(throwable -> log.error("Error listening to Redis topic.", throwable))
				.doFinally(signalType -> log.info("Stopped Listener. Signal Type: {}", signalType))
				.subscribe();
		};
	}

}
