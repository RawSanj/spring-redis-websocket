package com.github.rawsanj.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "raw", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Topic topic = new Topic();

    public static class Topic {
        private String message;
        private String count;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public Topic getTopic() {
        return topic;
    }
}
