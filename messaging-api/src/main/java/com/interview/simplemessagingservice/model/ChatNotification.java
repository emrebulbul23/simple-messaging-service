package com.interview.simplemessagingservice.model;

public class ChatNotification {
    private String id;
    private String senderName;

    /**
     * Used by WebSocketConfig configureMessageConverters method.
     * DO NOT REMOVE!
     */
    public ChatNotification() {

    }

    /**
     * Constructor
     *
     * @param id Id of the {@link ChatMessage} object.
     * @param senderName Name of the sender.
     */
    public ChatNotification(String id, String senderName) {
        this.id = id;
        this.senderName = senderName;
    }

    /**
     * Get id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Set id.
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get sender name.
     *
     * @return sender name
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Set sender name.
     *
     * @param senderName sender name
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
