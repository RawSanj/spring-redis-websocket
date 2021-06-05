package com.github.rawsanj.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class ObjectStringConverter {

	private final ObjectMapper objectMapper;

	public ObjectStringConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public <T> Mono<T> stringToObject(String data, Class<T> clazz) {
		return Mono.fromCallable(() -> objectMapper.readValue(data, clazz))
			.doOnError(throwable -> log.error("Error converting [{}] to class '{}'.", data, clazz.getSimpleName()));
	}

	public <T> Mono<String> objectToString(T object) {
		return Mono.fromCallable(() -> objectMapper.writeValueAsString(object))
			.doOnError(throwable -> log.error("Error converting [{}] to String.", object));
	}

}
