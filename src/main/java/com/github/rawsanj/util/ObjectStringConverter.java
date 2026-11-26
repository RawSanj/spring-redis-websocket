package com.github.rawsanj.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class ObjectStringConverter {

	private final JsonMapper jsonMapper;

	public ObjectStringConverter(JsonMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

	public <T> Mono<T> stringToObject(String data, Class<T> clazz) {
		return Mono.fromCallable(() -> jsonMapper.readValue(data, clazz))
			.doOnError(throwable -> log.error("Error converting [{}] to class '{}'.", data, clazz.getSimpleName(), throwable));
	}

	public <T> Mono<String> objectToString(T object) {
		return Mono.fromCallable(() -> jsonMapper.writeValueAsString(object))
			.doOnError(throwable -> log.error("Error converting [{}] to String.", object, throwable));
	}

}
