package stomp_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(MyStompSessionHandler.class);
    private String userId = "/";

    public MyStompSessionHandler(String userId) {
        this.userId = this.userId + userId;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
        session.subscribe("/topic/public" + this.userId, this);
        session.subscribe("/topic/public", this);
        session.send("/app/chat", new ChatNotification("Handshake", "Client"));
        logger.info("Subscribed");
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatNotification.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ChatNotification msg = (ChatNotification) payload;
        logger.info("Received! message-id: " + msg.getId() + " from : " + msg.getSenderName());
    }
}
