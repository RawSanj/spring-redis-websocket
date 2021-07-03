package com.github.rawsanj.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rawsanj.handler.WebHttpHandler;
import com.github.rawsanj.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringRedisWebSocketApplicationIT extends AbstractRedisContainerBaseTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ObjectMapper objectMapper;
	private WebTestClient testClient;

	@LocalServerPort
	private int port;

	private final String message = "Hello World";
	private final String messageResponse = "Message Sent Successfully!.";

	@BeforeEach
	void setupWebClient() {
		testClient = WebTestClient.bindToApplicationContext(applicationContext).build();
	}

	@Test
	void testPostMessage() {

		String webSocketConnectionUrl = "http://localhost:" + port + "/redis-chat";
		AtomicInteger messageCounter = new AtomicInteger(0);
		int messageCount = 5;

		WebSocketClient webSocketClient = new ReactorNettyWebSocketClient();
		Mono<Void> executeWebSocketConnectMono = webSocketClient.execute(URI.create(webSocketConnectionUrl),
			session -> session.receive()
				.doOnNext(webSocketMessage -> {
					try {
						log.info("Message Received in Test Connection: {}", webSocketMessage.getPayloadAsText());
						ChatMessage chatMessage = objectMapper.readValue(webSocketMessage.getPayloadAsText(), ChatMessage.class);
						assertThat(chatMessage.getMessage()).isEqualTo(message);
						assertThat(chatMessage.getId()).isEqualTo(messageCounter.incrementAndGet());
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				})
				.then(session.close())
		).timeout(Duration.ofSeconds(5)); // Assuming it takes 1 seconds to process one message to be on safer side.

		StepVerifier.create(executeWebSocketConnectMono)
			.then(() -> postMessages(messageCount))
			.expectNextCount(0)
			.expectError(TimeoutException.class)
			.verify();

		assertThat(messageCounter.get()).isEqualTo(messageCount);
	}

	private void postMessages(int messageCount) {
		IntStream.range(0, messageCount)
			.forEach(ignored -> testClient.post()
				.uri("/message")
				.bodyValue(new WebHttpHandler.Message(message))
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(WebHttpHandler.Message.class)
				.isEqualTo(new WebHttpHandler.Message(messageResponse)));
	}

}
