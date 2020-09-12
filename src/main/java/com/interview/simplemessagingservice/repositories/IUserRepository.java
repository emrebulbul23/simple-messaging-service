package com.interview.simplemessagingservice.repositories;

import com.interview.simplemessagingservice.model.SimpleUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/*
 * User Collection.
 */
@Repository
public interface IUserRepository extends MongoRepository<SimpleUser, String> {
    /**
     * Gets the user by username from MongoDB instance.
     * @param username name of SimpleUser
     * @return SimpleUser object
     */
    SimpleUser findByUsername(String username);

    /**
     * Checks if given username exists in MongoDB instance.
     * @param username name of SimpleUser
     * @return SimpleUser object
     */
    Boolean existsByUsername(String username);
}