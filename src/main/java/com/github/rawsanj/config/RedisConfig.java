package com.github.rawsanj.config;

import com.github.rawsanj.listener.RedisReceiver;
import com.github.rawsanj.service.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

@Configuration
public class RedisConfig {

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
											@Qualifier("chatMessageListenerAdapter") MessageListenerAdapter chatMessageListenerAdapter,
											@Qualifier("countListenerAdapter") MessageListenerAdapter countListenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(chatMessageListenerAdapter, new PatternTopic("chat"));
		container.addMessageListener(countListenerAdapter, new PatternTopic("count"));
		return container;
	}

	@Bean("chatMessageListenerAdapter")
	MessageListenerAdapter chatMessageListenerAdapter(RedisReceiver redisReceiver) {
		return new MessageListenerAdapter(redisReceiver, "receiveChatMessage");
	}

	@Bean("countListenerAdapter")
	MessageListenerAdapter countListenerAdapter(RedisReceiver redisReceiver) {
		return new MessageListenerAdapter(redisReceiver, "receiveCountMessage");
	}

	@Bean
	RedisReceiver receiver(WebSocketMessageService webSocketMessageService) {
		return new RedisReceiver(webSocketMessageService);
	}

	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

	@Bean
	RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;
	}

	// Redis Atomic Counter to store no. of total messages sent from multiple app instances.
	@Bean
	RedisAtomicInteger getChatMessageCounter(RedisTemplate redisTemplate) {
		RedisAtomicInteger chatMessageCounter = new RedisAtomicInteger("total-chat-message", redisTemplate.getConnectionFactory());
		return chatMessageCounter;
	}

}
