package com.github.rawsanj.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "raw", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties {

    private Topic topic = new Topic();

	@Getter
	@Setter
    public static class Topic {
        private String message;
        private String count;
    }

}
