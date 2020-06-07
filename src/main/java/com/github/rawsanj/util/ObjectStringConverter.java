package com.github.rawsanj.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class ObjectStringConverter {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static <T> Mono<T> stringToObject(String data, Class<T> clazz) {
		return Mono.fromCallable(() -> objectMapper.readValue(data, clazz))
			.doOnError(throwable -> log.error("Error converting [{}] to class '{}'.", data, clazz.getSimpleName()));
	}

	public static <T> Mono<String> objectToString(T object) {
		return Mono.fromCallable(() -> objectMapper.writeValueAsString(object))
			.doOnError(throwable -> log.error("Error converting [{}] to String.", object));
	}

}
