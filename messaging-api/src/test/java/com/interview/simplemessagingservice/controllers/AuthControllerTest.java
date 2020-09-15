package com.interview.simplemessagingservice.controllers;

import com.interview.simplemessagingservice.model.SimpleUser;
import com.interview.simplemessagingservice.response.JwtResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.data.mongodb.database=testdbm",
                "spring.data.mongodb.host=localhost"
        })
class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("simpleUser");
    }

    @Test
    void testRegisterAndSignUser() {
        ResponseEntity<String> stringResponseEntity = this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signup?username=testUser&password=123", null, String.class);
        assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());

        stringResponseEntity = this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signup?username=testUser&password=123", null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, stringResponseEntity.getStatusCode());

        ResponseEntity<JwtResponse> jwtResponseEntity = this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signin?username=testUser&password=123", null, JwtResponse.class);
        assertEquals("testUser", Objects.requireNonNull(jwtResponseEntity.getBody()).getUsername());
        assertNotNull(Objects.requireNonNull(jwtResponseEntity.getBody()).getToken());

        stringResponseEntity = this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signin?username=testUser2&password=123", null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, stringResponseEntity.getStatusCode());
    }

    @Test
    void blockUser() {
        addUser("testUser", "123");
        addUser("testUser2", "123");

        authorizeUser("testUser", "123");

        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> resp = this.restTemplate.exchange(
                "http://localhost:" + port + "/api/auth/blockUser/testUser4",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<String>() {
                });
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("User to be blocked cannot be found", resp.getBody());

        resp = this.restTemplate.exchange(
                "http://localhost:" + port + "/api/auth/blockUser/testUser2",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<String>() {
                });
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("testUser2 blocked successfully by testUser", resp.getBody());

        resp = this.restTemplate.exchange(
                "http://localhost:" + port + "/api/auth/blockUser/testUser2",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<String>() {
                });
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("User is already blocked!", resp.getBody());
    }

    @Test
    void getBlockedUsersList() {
        addUser("testUser", "123");
        addUser("testUser2", "123");
        addUser("testUser3", "123");

        authorizeUser("testUser", "123");

        blockUser("testUser2");
        blockUser("testUser3");

        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<List<String>> exchange = this.restTemplate.exchange(
                "http://localhost:" + port + "/api/auth/blockedUsers",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<String>>() {
                });
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
        assertEquals("testUser2", Objects.requireNonNull(exchange.getBody()).get(0));
        assertEquals("testUser3", Objects.requireNonNull(exchange.getBody()).get(1));
    }

    private void addUser(String username, String password) {
        this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signup?username=" + username + "&password=" + password, null, String.class);
    }

    private void authorizeUser(String username, String password) {
        // authenticate
        ResponseEntity<JwtResponse> jwtResponseEntity = this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signin?username=" + username + "&password=" + password, null, JwtResponse.class);
        headers.add("Authorization", "Bearer " +
                Objects.requireNonNull(jwtResponseEntity.getBody()).getToken());
    }

    private void blockUser(String username) {
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> resp = this.restTemplate.exchange(
                "http://localhost:" + port + "/api/auth/blockUser/" + username,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<String>() {
                });
    }
}