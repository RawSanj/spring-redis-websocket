package com.github.rawsanj.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.GenericContainer;

@SpringBootTest
@ContextConfiguration(initializers = AbstractRedisContainerBaseTest.Initializer.class)
public class AbstractRedisContainerBaseTest {

	private static final String REDIS_IMAGE = "bitnami/redis:latest";
	private static final String REDIS_PASSWORD = "SuperSecretRedisPassword";
	private static final Integer REDIS_PORT = 6379;

	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		private static final GenericContainer redis = new GenericContainer<>(REDIS_IMAGE)
			.withExposedPorts(REDIS_PORT)
			.withEnv("REDIS_PASSWORD", REDIS_PASSWORD)
			.withReuse(true);

		@Override
		public void initialize(ConfigurableApplicationContext context) {
			// Start container
			redis.start();

			// Override Redis configuration
			String redisContainerIP = "spring.redis.host=" + redis.getContainerIpAddress();
			String redisContainerPort = "spring.redis.port=" + redis.getMappedPort(REDIS_PORT);
			String redisContainerPassword = "spring.redis.password=" + REDIS_PASSWORD;
			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context, redisContainerIP, redisContainerPort, redisContainerPassword); // <- This is how you override the configuration in runtime.
		}
	}
}

