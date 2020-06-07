package com.github.rawsanj.handler;

import com.github.rawsanj.messaging.RedisChatMessagePublisher;
import com.github.rawsanj.model.ChatMessage;
import com.github.rawsanj.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

	private final EmitterProcessor<ChatMessage> messageEmitterProcessor;
	private final FluxSink<ChatMessage> chatMessageFluxSink;
	private final RedisChatMessagePublisher redisChatMessagePublisher;

	public ChatWebSocketHandler(EmitterProcessor<ChatMessage> messageEmitterProcessor, RedisChatMessagePublisher redisChatMessagePublisher) {
		this.messageEmitterProcessor = messageEmitterProcessor;
		this.chatMessageFluxSink = messageEmitterProcessor.sink();
		this.redisChatMessagePublisher = redisChatMessagePublisher;
	}

	@Override
	public Mono<Void> handle(WebSocketSession webSocketSession) {

		Flux<WebSocketMessage> sendMessageFlux = messageEmitterProcessor.flatMap(ObjectStringConverter::objectToString).map(webSocketSession::textMessage);
		Mono<Void> outputMessage = webSocketSession.send(sendMessageFlux);
		Mono<Void> inputMessage = webSocketSession.receive()
			.flatMap(webSocketMessage -> redisChatMessagePublisher.publishChatMessage(webSocketMessage.getPayloadAsText()))
			.then();

		return Mono.zip(inputMessage, outputMessage).then();
	}

	public Mono<Void> sendMessage(ChatMessage chatMessage) {
		return Mono.fromSupplier(() -> chatMessageFluxSink.next(chatMessage)).then();
	}

}
