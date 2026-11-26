package com.github.rawsanj.messaging;

import com.github.rawsanj.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.github.rawsanj.config.ChatConstants.MESSAGE_TOPIC;

@Component
@Slf4j
public class RedisChatMessagePublisher {

	private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
	private final RedisAtomicInteger chatMessageCounter;
	private final RedisAtomicLong activeUserCounter;
	private final JsonMapper jsonMapper;

	public RedisChatMessagePublisher(ReactiveStringRedisTemplate reactiveStringRedisTemplate, RedisAtomicInteger chatMessageCounter, RedisAtomicLong activeUserCounter, JsonMapper jsonMapper) {
		this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
		this.chatMessageCounter = chatMessageCounter;
		this.activeUserCounter = activeUserCounter;
		this.jsonMapper = jsonMapper;
	}

	public Mono<Long> publishChatMessage(String message) {
		Integer totalChatMessage = chatMessageCounter.incrementAndGet();
		return Mono.fromCallable(() -> {
			try {
				return InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				log.error("Error getting hostname.", e);
			}
			return "localhost";
		}).map(hostName -> {
			ChatMessage chatMessage = new ChatMessage(totalChatMessage, message, hostName, activeUserCounter.get());
			return jsonMapper.writeValueAsString(chatMessage);
		}).flatMap(chatString -> {
			// Publish Message to Redis Channels
			return reactiveStringRedisTemplate.convertAndSend(MESSAGE_TOPIC, chatString)
				.doOnSuccess(_ -> log.debug("Total of {} Messages published to Redis Topic.", totalChatMessage))
				.doOnError(throwable -> log.error("Error publishing message.", throwable));
		});
	}

}
