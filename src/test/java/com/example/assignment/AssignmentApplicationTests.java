package com.example.assignment;

import com.example.assignment.dto.request.LoginReq;
import com.example.assignment.dto.response.AuthRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = AssignmentApplication.class)
@ActiveProfiles("test")
public abstract class AssignmentApplicationTests {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String getUrl(String path) {
        return "http://localhost:" + port + path;
    }


    @Test
    void contextLoads() {
    }

    protected String authenticateAndGetToken(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        LoginReq credentials = new LoginReq();
        credentials.setEmail(email);
        credentials.setPassword(password);

        ResponseEntity<AuthRes> response = restTemplate.exchange(
            getUrl("/api/v1/auth/login"),
            HttpMethod.POST,
            new HttpEntity<>(credentials, headers),
            AuthRes.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        return response.getBody().getAccessToken();
    }

}
