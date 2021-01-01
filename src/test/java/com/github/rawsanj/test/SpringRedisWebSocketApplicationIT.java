package com.github.rawsanj.test;

import com.github.rawsanj.handler.WebHttpHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
public class SpringRedisWebSocketApplicationIT extends AbstractRedisContainerBaseTest {

	@Autowired
	private ApplicationContext applicationContext;

	private WebTestClient testClient;

	@BeforeEach
	void setupWebClient() {
		testClient = WebTestClient.bindToApplicationContext(applicationContext).build();
	}

	@Test
	void testPostMessage() {

		testClient.post()
			.uri("/message")
			.bodyValue(new WebHttpHandler.Message("Hello World"))
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody(WebHttpHandler.Message.class)
			.isEqualTo(new WebHttpHandler.Message("Message Sent Successfully!."));

	}

}
