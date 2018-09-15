package com.github.rawsanj.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rawsanj.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    private final RedisAtomicInteger chatMessageCounter;
    private final StringRedisTemplate stringRedisTemplate;

    public MessageController(RedisAtomicInteger chatMessageCounter, StringRedisTemplate stringRedisTemplate) {
        this.chatMessageCounter = chatMessageCounter;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @MessageMapping("/message")
    public void sendWsChatMessage(String message) throws JsonProcessingException {
        LOGGER.info("Incoming WebSocket Message : {}", message);

        publishMessageToRedis(message);
    }

    @PostMapping("/message")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendHttpChatHttpMessage(@RequestBody Map<String, String> message) throws JsonProcessingException {
        String httpMessage = message.get("message");
        LOGGER.info("Incoming HTTP Message : {}", httpMessage);
        publishMessageToRedis(httpMessage);

        Map<String, String> response = new HashMap<>();
        response.put("response", "Message Sent Successfully over HTTP");

        return ResponseEntity.ok(response);
    }

    private void publishMessageToRedis(String message) throws JsonProcessingException {

        Integer totalChatMessage = chatMessageCounter.incrementAndGet();
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostName = "localhost";
        }

        ChatMessage chatMessage = new ChatMessage(totalChatMessage, message, hostName);
        ObjectMapper objectMapper = new ObjectMapper();
        String chatString = objectMapper.writeValueAsString(chatMessage);

        // Publish Message to Redis Channels
        stringRedisTemplate.convertAndSend("chat", chatString);
        stringRedisTemplate.convertAndSend("count", totalChatMessage.toString());

    }
}
