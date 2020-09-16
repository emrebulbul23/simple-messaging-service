package stomp_client;

public class ChatNotification {
    private String id;
    private String senderName;

    /**
     * Used by WebSocketConfig configureMessageConverters method.
     * DO NOT REMOVE!
     */
    public ChatNotification() {

    }

    public ChatNotification(String id, String senderName) {
        this.id = id;
        this.senderName = senderName;
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
}
