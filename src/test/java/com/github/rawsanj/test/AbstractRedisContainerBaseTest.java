package com.github.rawsanj.test;

import com.github.rawsanj.SpringRedisWebSocketApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.GenericContainer;

@Slf4j
@SpringBootTest
@ContextConfiguration(
	initializers = AbstractRedisContainerBaseTest.Initializer.class,
	classes = {SpringRedisWebSocketApplication.class, AbstractRedisContainerBaseTest.ApplicationStoppedListener.class}
)
public class AbstractRedisContainerBaseTest {

	private static final String REDIS_IMAGE = "bitnami/redis:6.0.9";
	private static final String REDIS_PASSWORD = "SuperSecretRedisPassword";
	private static final Integer REDIS_PORT = 6379;

	private static final GenericContainer<?> redisContainer = new GenericContainer<>(REDIS_IMAGE)
		.withExposedPorts(REDIS_PORT)
		.withEnv("REDIS_PASSWORD", REDIS_PASSWORD)
		.withReuse(true);

	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext context) {
			// Start container
			redisContainer.start();

			// Override Redis configuration
			String redisContainerIP = "spring.data.redis.host=" + redisContainer.getHost();
			String redisContainerPort = "spring.data.redis.port=" + redisContainer.getMappedPort(REDIS_PORT);
			String redisContainerPassword = "spring.data.redis.password=" + REDIS_PASSWORD;
			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context, redisContainerIP, redisContainerPort, redisContainerPassword); // <- This is how you override the configuration in runtime.
		}
	}

	@TestComponent
	public static class ApplicationStoppedListener implements DisposableBean {
		@Override
		public void destroy() {
			log.info("Application Stopped. Stopping Redis Container...");
			redisContainer.stop();
		}
	}
}

