package com.interview.simplemessagingservice.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class ChatMessage {
    @Id
    private String id;
    private String senderName;
    private String recipientName;
    private String content;
    private Date timestamp;
    private MessageStatus status;

    public ChatMessage(String senderName, String recipientName, String content) {
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.content = content;
        this.timestamp = new Date();
        this.status = MessageStatus.DELIVERED;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", senderName='" + senderName + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
