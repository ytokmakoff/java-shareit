package ru.practicum.shareitgateway.client;

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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void get_withPathOnly_shouldCallGetMethod() {
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get(path);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void get_withPathAndUserId_shouldCallGetMethod() {
        String path = "/test";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        // Настройка поведения RestTemplate
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // Вызов метода
        ResponseEntity<Object> response = baseClient.get(path, userId);

        // Проверка
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void get_withPathUserIdAndParameters_shouldCallGetMethod() {
        String path = "/test";
        long userId = 1L;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param", "value");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        // Настройка поведения RestTemplate
        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        // Вызов метода
        ResponseEntity<Object> response = baseClient.get(path, userId, parameters);

        // Проверка
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void post_withPathOnly_shouldCallPostMethod() {
        String path = "/test";
        Object requestBody = new Object();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        // Настройка поведения RestTemplate
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // Вызов метода
        ResponseEntity<Object> response = baseClient.post(path, requestBody);

        // Проверка
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void post_withPathAndUserId_shouldCallPostMethod() {
        String path = "/test";
        long userId = 1L;
        Object requestBody = new Object();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        // Настройка поведения RestTemplate
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // Вызов метода
        ResponseEntity<Object> response = baseClient.post(path, userId, requestBody);

        // Проверка
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void put_withPathAndUserId_shouldCallPutMethod() {
        String path = "/test";
        long userId = 1L;
        Object requestBody = new Object();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        // Настройка поведения RestTemplate
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // Вызов метода
        ResponseEntity<Object> response = baseClient.put(path, userId, requestBody);

        // Проверка
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patch_withPathAndUserId_shouldCallPatchMethod() {
        String path = "/test";
        long userId = 1L;
        Object requestBody = new Object();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        // Настройка поведения RestTemplate
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // Вызов метода
        ResponseEntity<Object> response = baseClient.patch(path, userId, requestBody);

        // Проверка
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void delete_withPathOnly_shouldCallDeleteMethod() {
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        // Настройка поведения RestTemplate
        when(restTemplate.exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // Вызов метода
        ResponseEntity<Object> response = baseClient.delete(path);

        // Проверка
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void delete_withPathAndUserId_shouldCallDeleteMethod() {
        String path = "/test";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        // Настройка поведения RestTemplate
        when(restTemplate.exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        // Вызов метода
        ResponseEntity<Object> response = baseClient.delete(path, userId);

        // Проверка
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void makeAndSendRequest_shouldHandleHttpStatusCodeException() {
        String path = "/test";
        long userId = 1L;
        Object requestBody = new Object();

        // Настройка поведения RestTemplate, чтобы выбрасывать исключение
        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(new HttpStatusCodeException(HttpStatus.BAD_REQUEST) {
                });

        // Вызов метода
        ResponseEntity<Object> response = baseClient.post(path, userId, requestBody);

        // Проверка, что ответ содержит статус BAD_REQUEST
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void patchWithBody_ShouldReturnResponseEntityWithOkStatus() {
        String path = "/test";
        String body = "{\"key\": \"value\"}";

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = baseClient.patch(path, body);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate, times(1)).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patchWithUserId_ShouldReturnResponseEntityWithNoContentStatus() {
        String path = "/test";
        long userId = 1L;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = baseClient.patch(path, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(restTemplate, times(1)).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patchWithBody_ShouldReturnResponseEntityWithBadRequestStatus() {
        String path = "/test";
        String body = "{\"key\": \"value\"}";

        // Имитируем ответ с кодом 400 BAD_REQUEST
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = baseClient.patch(path, body);

        // Проверяем, что статус ответа равен 400 BAD_REQUEST
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void patchWithBody_ShouldReturnBadRequestStatusAndErrorMessage() {
        String path = "/test";
        String body = "{\"key\": \"value\"}";

        String errorMessage = "Error occurred";
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = baseClient.patch(path, body);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }
}

