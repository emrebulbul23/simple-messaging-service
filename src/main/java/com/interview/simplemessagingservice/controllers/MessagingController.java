package com.interview.simplemessagingservice.controllers;

import com.interview.simplemessagingservice.model.ChatMessage;
import com.interview.simplemessagingservice.repositories.IChatMessageRepository;
import com.interview.simplemessagingservice.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("msg")
public class MessagingController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Autowired IChatMessageRepository
     */
    @Autowired
    private IChatMessageRepository chatMessageRepository;

    /**
     * Get history of messages sent by the authenticated user. If username argument
     * is not empty return only the messages sent to that user.
     * @param username For filtering messages sent to the given username.
     * @return List<ChatMessage>
     */
    @Operation(summary = "Get message history", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "")
    public List<ChatMessage> getMessageHistory(
            @RequestParam(name = "usarname", required = false, defaultValue = "") String username) {
        // Get authorized user's name to filter messages
        String loggedUser = CommonUtil.getInstance().getAuthenticatedUsersUsername();
        Stream<ChatMessage> chatMessageStream = chatMessageRepository.findAll().stream()
                .filter(msg -> msg.getSenderName().equals(loggedUser));
        if(username.isEmpty()){
            logger.info(MessageFormat.format("Message history for user {0} is returned",
                    loggedUser));
            return chatMessageStream.collect(Collectors.toList());
        }
        logger.info(MessageFormat.format("Message history from user {0} to user {1} is returned",
                loggedUser, username));
        return chatMessageStream.filter(msg -> msg.getRecipientName().equals(username)).collect(Collectors.toList());
    }

    @Operation(summary = "Get message history", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "")
    public String sendMessage(){

        return "";
    }
}
