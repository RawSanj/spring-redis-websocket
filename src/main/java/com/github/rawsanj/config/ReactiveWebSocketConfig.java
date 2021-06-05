package com.github.rawsanj.config;

import com.github.rawsanj.handler.ChatWebSocketHandler;
import com.github.rawsanj.messaging.RedisChatMessagePublisher;
import com.github.rawsanj.model.ChatMessage;
import com.github.rawsanj.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.github.rawsanj.config.ChatConstants.WEBSOCKET_MESSAGE_MAPPING;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class ReactiveWebSocketConfig {

	@Bean
	public ChatWebSocketHandler webSocketHandler(RedisChatMessagePublisher redisChatMessagePublisher, RedisAtomicLong activeUserCounter,
												 ObjectStringConverter objectStringConverter) {
		Sinks.Many<ChatMessage> chatMessageSink = Sinks.many().multicast().onBackpressureBuffer();
		return new ChatWebSocketHandler(chatMessageSink, redisChatMessagePublisher, activeUserCounter, objectStringConverter);
	}

	@Bean
	public HandlerMapping webSocketHandlerMapping(ChatWebSocketHandler webSocketHandler) {
		Map<String, WebSocketHandler> map = new HashMap<>();
		map.put(WEBSOCKET_MESSAGE_MAPPING, webSocketHandler);
		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setCorsConfigurations(Collections.singletonMap("*", new CorsConfiguration().applyPermitDefaultValues()));
		handlerMapping.setOrder(1);
		handlerMapping.setUrlMap(map);
		return handlerMapping;
	}

	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

}
