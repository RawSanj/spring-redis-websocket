package com.github.rawsanj.messaging;

import com.github.rawsanj.handler.ChatWebSocketHandler;
import com.github.rawsanj.model.ChatMessage;
import com.github.rawsanj.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.github.rawsanj.config.ChatConstants.MESSAGE_TOPIC;

@Component
@Slf4j
public class RedisChatMessageListener {

	private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
	private final ChatWebSocketHandler chatWebSocketHandler;

	public RedisChatMessageListener(ReactiveStringRedisTemplate reactiveStringRedisTemplate, ChatWebSocketHandler chatWebSocketHandler) {
		this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
		this.chatWebSocketHandler = chatWebSocketHandler;
	}

	public Mono<Void> subscribeMessageChannelAndPublishOnWebSocket() {
		return reactiveStringRedisTemplate.listenTo(new PatternTopic(MESSAGE_TOPIC))
			.map(ReactiveSubscription.Message::getMessage)
			.flatMap(message -> ObjectStringConverter.stringToObject(message, ChatMessage.class))
			.filter(chatMessage -> !chatMessage.getMessage().isEmpty())
			.flatMap(chatWebSocketHandler::sendMessage)
			.then();
	}

}
