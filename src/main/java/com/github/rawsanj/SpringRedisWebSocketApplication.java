package com.github.rawsanj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class SpringRedisWebSocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRedisWebSocketApplication.class, args);
	}

}
