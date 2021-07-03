package com.github.rawsanj.handler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.rawsanj.messaging.RedisChatMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration(proxyBeanMethods=false)
public class WebHttpHandler {

	@Bean
	public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/static/index.html") Resource html, RedisChatMessagePublisher redisChatMessagePublisher) {
		return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html))
			.andRoute(POST("/message"), request -> request.bodyToMono(Message.class)
				.flatMap(message -> redisChatMessagePublisher.publishChatMessage(message.getMessage()))
				.flatMap(aLong -> ServerResponse.ok().bodyValue(new Message("Message Sent Successfully!."))));
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
	public static class Message {
		private String message;
	}

}
