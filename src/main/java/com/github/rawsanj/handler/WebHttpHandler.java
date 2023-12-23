package com.github.rawsanj.handler;

import com.github.rawsanj.messaging.RedisChatMessagePublisher;
import com.github.rawsanj.model.Message;
import com.github.rawsanj.model.Platform;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration(proxyBeanMethods = false)
public class WebHttpHandler {

	@Bean
	public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/static/index.html") Resource html,
													 RedisChatMessagePublisher redisChatMessagePublisher, Environment environment) {
		return route(GET("/"), request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html))
			.andRoute(POST("/message"), request -> request.bodyToMono(Message.class)
				.flatMap(message -> redisChatMessagePublisher.publishChatMessage(message.getMessage()))
				.flatMap(aLong -> ServerResponse.ok().bodyValue(new Message("Message Sent Successfully!.")))
			)
			.andRoute(
				GET("/platform"), request -> ServerResponse.ok().bodyValue(new Platform(environment.getProperty("RUNTIME_PLATFORM", "JVM")))
			);
	}

}
