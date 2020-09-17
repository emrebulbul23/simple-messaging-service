package com.interview.simplemessagingservice.controllers;

import com.interview.simplemessagingservice.model.ChatMessage;
import com.interview.simplemessagingservice.model.SimpleUser;
import com.interview.simplemessagingservice.response.JwtResponse;
import org.junit.jupiter.api.AfterEach;
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
class MessagingControllerTest {
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
        this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signup?username=testUser&password=123", null, String.class);
        this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signup?username=testUser2&password=123", null, String.class);
        this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signup?username=testUser3&password=123", null, String.class);

        ResponseEntity<JwtResponse> jwtResponseEntity = this.restTemplate.postForEntity("http://localhost:" + port +
                "/api/auth/signin?username=testUser&password=123", null, JwtResponse.class);
        headers.add("Authorization", "Bearer " +
                Objects.requireNonNull(jwtResponseEntity.getBody()).getToken());
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection("chatMessage");
    }

    @Test
    void getMessageHistory() {
        sendMessage("testUser2", "test1");
        sendMessage("testUser2", "test2");
        sendMessage("testUser3", "test3");

        HttpEntity<ChatMessage> request = new HttpEntity<>(null, headers);
        ResponseEntity<List<ChatMessage>> responseEntity = this.restTemplate.exchange(
                "http://localhost:" + port + "/msg",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ChatMessage>>() {
                });
        List<ChatMessage> body = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("testUser", Objects.requireNonNull(body).get(0).getSenderName());
        assertEquals("testUser", Objects.requireNonNull(body).get(1).getSenderName());
        assertEquals(3, Objects.requireNonNull(body).size());

        responseEntity = this.restTemplate.exchange(
                "http://localhost:" + port + "/msg?username=testUser2",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ChatMessage>>() {
                });
        body = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, Objects.requireNonNull(body).size());

        // block user
        blockUser("testUser3");
        ResponseEntity<String> responseEntity2 = this.restTemplate.exchange(
                "http://localhost:" + port + "/msg?username=testUser3",
                HttpMethod.GET,
                request,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity2.getStatusCode());
    }

    @Test
    void sendMessage() {
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        // receiver not found
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                "http://localhost:" + port +
                        "/msg?receiver=testUser4&content=test",
                HttpMethod.POST,
                request,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Receiver cannot be found! Username: testUser4", responseEntity.getBody());

        // receiver found
        responseEntity = this.restTemplate.exchange(
                "http://localhost:" + port +
                        "/msg?receiver=testUser2&content=test",
                HttpMethod.POST,
                request,
                String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Message successfully sent to testUser2", responseEntity.getBody());
    }

    @Test
    void getMessageWithId() {
        sendMessage("testUser2", "test1");
        sendMessage("testUser3", "test3");

        HttpEntity<ChatMessage> request = new HttpEntity<>(null, headers);
        ResponseEntity<List<ChatMessage>> responseEntity = this.restTemplate.exchange(
                "http://localhost:" + port + "/msg?username=testUser2",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ChatMessage>>() {
                });
        String id = Objects.requireNonNull(responseEntity.getBody()).get(0).getId();


        ResponseEntity<ChatMessage> responseEntitySingle = this.restTemplate.exchange(
                "http://localhost:" + port + "/msg/" + id,
                HttpMethod.GET,
                request,
                ChatMessage.class);
        assertEquals(HttpStatus.OK, responseEntitySingle.getStatusCode());
        assertEquals("test1", responseEntitySingle.getBody().getContent());

        // block user
        responseEntity = this.restTemplate.exchange(
                "http://localhost:" + port + "/msg?username=testUser3",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ChatMessage>>() {
                });
        id = Objects.requireNonNull(responseEntity.getBody()).get(0).getId();

        blockUser("testUser3");
        ResponseEntity<String> responseEntity2 = this.restTemplate.exchange(
                "http://localhost:" + port + "/msg?username=testUser3",
                HttpMethod.GET,
                request,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity2.getStatusCode());
    }

    private ResponseEntity<String> sendMessage(String to, String cont) {
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                "http://localhost:" + port +
                        "/msg?receiver=" + to + "&content=" + cont,
                HttpMethod.POST,
                request,
                String.class);
        return responseEntity;
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