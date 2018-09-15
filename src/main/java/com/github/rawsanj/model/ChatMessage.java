package com.github.rawsanj.model;

public class ChatMessage {

    private Integer id;
    private String message;
    private String hostname;

    public ChatMessage(Integer id, String message, String hostname) {
        this.id = id;
        this.message = message;
        this.hostname = hostname;
    }

    public ChatMessage() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", hostname='" + hostname + '\'' +
                '}';
    }
}
