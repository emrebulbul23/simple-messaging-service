package com.interview.simplemessagingservice.repositories;

import com.interview.simplemessagingservice.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
