package com.github.rawsanj.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rawsanj.model.ChatMessage;
import com.github.rawsanj.service.WebSocketMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RedisReceiver {

    private static final Logger log = LoggerFactory.getLogger(RedisReceiver.class);

    private final WebSocketMessageService webSocketMessageService;

    public RedisReceiver(WebSocketMessageService webSocketMessageService) {
        this.webSocketMessageService = webSocketMessageService;
    }

    // Invoked when message is publish to "chat" channel
    public void receiveChatMessage(String message) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);

        log.info("Notification Message Received: " + chatMessage);
        webSocketMessageService.sendChatMessage(chatMessage);

    }

    // Invoked when message is publish to "count" channel
    public void receiveCountMessage(String totalMessageCount) {

        log.info("Count Message Received :" + totalMessageCount);
        webSocketMessageService.sendMessageCount(Integer.parseInt(totalMessageCount));

    }
}
