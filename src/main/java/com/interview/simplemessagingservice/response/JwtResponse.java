package com.interview.simplemessagingservice.response;

public class JwtResponse {
    private String token;
    private static final String type = "Bearer";
    private String username;

    /*
     * Private constructor to hide implicit public one.
     */
    private JwtResponse(){

    }

    /**
     * Constructor.
     * @param accessToken accessToken
     * @param username username
     */
    public JwtResponse(String accessToken, String username) {
        this.token = accessToken;
        this.username = username;
    }

    /**
     * Get token.
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set token.
     * @param token token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Get type.
     * @return type.
     */
    public static String getType() {
        return type;
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
}

