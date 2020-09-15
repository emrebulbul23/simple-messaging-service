package com.interview.simplemessagingservice.model;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;

public class SimpleUser {
    @Id
    private String id;

    private String username;
    private String password;
    private List<String> blockedUsers;

    /*
     * Private constructor to hide implicit public one.
     */
    private SimpleUser() {
    }

    /**
     * Constructor.
     * @param username username
     * @param password password
     */
    public SimpleUser(String username, String password) {
        this.username = username;
        this.password = password;
        this.blockedUsers = Collections.emptyList();
    }

    /**
     * Add newly blocked user's name to the list.
     * @param username username
     */
    public void addBlockedUser(String username){
        blockedUsers.add(username);
    }

    /**
     * Get blocked users list.
     * @return {@link List<String>}
     */
    public List<String> getBlockedUsers() {
        return blockedUsers;
    }

    /**
     * Get id.
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Set id
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get username.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username.
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get password.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password.
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
