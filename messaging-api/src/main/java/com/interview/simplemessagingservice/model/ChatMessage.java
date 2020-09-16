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

    /**
     * Constructor
     *
     * @param senderName sender name
     * @param recipientName recipient name
     * @param content
     */
    public ChatMessage(String senderName, String recipientName, String content) {
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.content = content;
        this.timestamp = new Date();
        this.status = MessageStatus.DELIVERED;
    }

    /**
     * To string
     * @return Stringified object
     */
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

    /**
     * Get id
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Set id
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get sender name
     *
     * @return sender name
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Set sender name
     *
     * @param senderName sender name
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Get recipient name
     *
     * @return recipient name
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * Set recipient name
     *
     * @param recipientName recipient name
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /**
     * Get content
     *
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set content
     *
     * @param content content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get time stamp
     *
     * @return time stamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Set time stamp
     *
     * @param timestamp time stamp
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get status
     *
     * @return status
     */
    public MessageStatus getStatus() {
        return status;
    }

    /**
     * Set status
     *
     * @param status status
     */
    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
