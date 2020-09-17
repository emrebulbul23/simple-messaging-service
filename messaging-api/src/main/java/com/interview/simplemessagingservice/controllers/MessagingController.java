package com.interview.simplemessagingservice.controllers;

import com.interview.simplemessagingservice.model.ChatMessage;
import com.interview.simplemessagingservice.model.ChatNotification;
import com.interview.simplemessagingservice.model.SimpleUser;
import com.interview.simplemessagingservice.repositories.IChatMessageRepository;
import com.interview.simplemessagingservice.repositories.IUserRepository;
import com.interview.simplemessagingservice.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("msg")
public class MessagingController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Autowired SimpMessagingTemplate
     */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Autowired IUserRepository
     */
    @Autowired
    public IUserRepository userRepository;

    /**
     * Autowired IChatMessageRepository
     */
    @Autowired
    private IChatMessageRepository chatMessageRepository;

    /**
     * Get history of messages sent by the authenticated user. If username argument
     * is not empty return only the messages sent to that user.
     *
     * @param username For filtering messages sent to the given username.
     * @return List<ChatMessage>
     */
    @Operation(summary = "Get message history", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "")
    public List<ChatMessage> getMessageHistory(
            @RequestParam(name = "username", required = false, defaultValue = "") String username) {
        // Get authorized user's name to filter messages
        String loggedUser = CommonUtil.getInstance().getAuthenticatedUsersUsername();
        Stream<ChatMessage> chatMessageStream = chatMessageRepository.findAll().stream()
                .filter(msg -> msg.getSenderName().equals(loggedUser));

        SimpleUser byUsername = userRepository.findByUsername(loggedUser);
        List<String> blockedUsers = byUsername.getBlockedUsers();
        if (username.isEmpty()) {
            // filter for blocked users
            Stream<ChatMessage> chatMessageStreamNew = chatMessageStream
                    .filter(msg -> !(blockedUsers.contains(msg.getSenderName()) ||
                            blockedUsers.contains(msg.getRecipientName())));

            logger.info(MessageFormat.format("Message history for user {0} is returned",
                    loggedUser));
            return chatMessageStreamNew.collect(Collectors.toList());
        }


        if (blockedUsers.contains(username)) {
            logger.error("Messages are from a blocked user!");
            throw new ResponseStatusException(NOT_FOUND, "User blocked before!");
        }

        logger.info(MessageFormat.format("Message history from user {0} to user {1} is returned",
                loggedUser, username));
        return chatMessageStream.filter(msg -> msg.getRecipientName().equals(username)).collect(Collectors.toList());
    }

    /**
     * Send message to the given receiver with the given content. If receiver is not
     * found returns http status 404.
     *
     * @param receiver Receiver's name
     * @param content  Message content
     * @return {@link ResponseEntity<String>}
     */
    @Operation(summary = "Send message", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "")
    public ResponseEntity<String> sendMessage(
            @RequestParam(name = "receiver") String receiver,
            @RequestParam(name = "content") String content) {
        // Check if receiver exists
        boolean exists = userRepository.existsByUsername(receiver);
        if (!exists) {
            String format = MessageFormat.format("Receiver cannot be found! Username: {0}", receiver);
            logger.error(format);
            return ResponseEntity.status(404).body(format);
        }

        // Get authorized user's name
        String loggedUser = CommonUtil.getInstance().getAuthenticatedUsersUsername();
        ChatMessage chatMessage = new ChatMessage(loggedUser, receiver, content);
        ChatMessage save = chatMessageRepository.save(chatMessage);

        /*
         * Notify user whom is subscribed to topic: "/user/{recipientId}/queue/messages"
         */
        SimpleUser receiverUser = userRepository.findByUsername(receiver);
        messagingTemplate.convertAndSend("/topic/public/"+receiverUser.getId(),
                new ChatNotification(
                        save.getId(),
                        save.getSenderName()));

        logger.info(MessageFormat.format("Message successfully sent! Msg: {0}", save));
        return ResponseEntity.ok(MessageFormat.format("Message successfully sent to {0}", receiver));
    }

    /**
     * Reply to websocket handshake messages.
     * @param message Message from client.
     * @return {@link ChatNotification}
     */
    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public ChatNotification send(ChatNotification message){
        logger.info("Websocket client is connected");
        return message;
    }

    /**
     * Get message by message id. It is used to access messages that their id
     * is pushed with the message notification. That way users can access messages
     * when they are received.
     *
     * @param messageId Id of the message to be fetched.
     * @return {@link ResponseEntity<ChatMessage>}
     */
    @Operation(summary = "Get message with id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "{messageId}")
    public ResponseEntity<ChatMessage> getMessageWithId(@PathVariable String messageId) {
        // Check if message exists
        Optional<ChatMessage> byId = chatMessageRepository.findById(messageId);
        if (byId.isEmpty()) {
            String format = MessageFormat.format("Message not found! MessageId: {0}", messageId);
            logger.error(format);
            throw new ResponseStatusException(NOT_FOUND, "Message not found!");
        }

        // Get authenticated user.
        String authenticatedUsersUsername = CommonUtil.getInstance().getAuthenticatedUsersUsername();
        SimpleUser byUsername = userRepository.findByUsername(authenticatedUsersUsername);

        // Check if message if from blocked user
        ChatMessage chatMessage = byId.get();
        if (byUsername.getBlockedUsers().contains(chatMessage.getSenderName()) ||
                byUsername.getBlockedUsers().contains(chatMessage.getRecipientName())) {
            String format = MessageFormat.format("Message is from a blocked user! MessageId: {0}", messageId);
            logger.error(format);
            throw new ResponseStatusException(NOT_FOUND, "Message not found!");
        }

        logger.info(MessageFormat.format("Message returned! MessageId: {0}", messageId));
        return ResponseEntity.ok().body(chatMessage);
    }
}
