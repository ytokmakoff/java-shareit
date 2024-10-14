package ru.practicum.shareitgateway.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BaseClient baseClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get_shouldCallRestTemplateGet() {
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.get(path);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void post_shouldCallRestTemplatePost() {
        String path = "/test";
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.post(path, body);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void put_shouldCallRestTemplatePut() {
        String path = "/test";
        String body = "request body";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.put(path, userId, body);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void delete_shouldCallRestTemplateDelete() {
        String path = "/test";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body("response");

        when(restTemplate.exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.delete(path, userId);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }
}
