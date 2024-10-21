package ru.practicum.shareitgateway.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.request.dto.ItemRequest;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        String serverUrl = "http://localhost:8080";

        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory((Supplier<ClientHttpRequestFactory>) any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        itemRequestClient = new ItemRequestClient(serverUrl, restTemplateBuilder);
    }

    @Test
    void save_ShouldReturnResponseEntityWithOkStatus() {
        long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemRequestClient.save(itemRequest, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void findByUserId_ShouldReturnResponseEntityWithOkStatus() {
        long userId = 1L;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemRequestClient.findByUserId(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void findAll_ShouldReturnResponseEntityWithOkStatus() {
        long userId = 1L;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemRequestClient.findAll(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void findById_ShouldReturnResponseEntityWithOkStatus() {
        long requestId = 123L;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemRequestClient.findById(requestId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }
}