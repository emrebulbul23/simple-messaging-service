package com.interview.simplemessagingservice.model;

import org.springframework.data.annotation.Id;

public class SimpleUser {
    @Id
    private String id;

    private String username;
    private String password;

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
