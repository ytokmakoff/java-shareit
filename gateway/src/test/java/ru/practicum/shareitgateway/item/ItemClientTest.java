package ru.practicum.shareitgateway.item;

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
import ru.practicum.shareitgateway.item.dto.*;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        String serverUrl = "http://localhost:8080";

        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory((Supplier<ClientHttpRequestFactory>) any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        itemClient = new ItemClient(serverUrl, restTemplateBuilder);
    }

    @Test
    void saveItem_ShouldReturnResponseEntityWithOkStatus() {
        long userId = 1L;
        ItemDto itemDto = new ItemDto();

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemClient.save(itemDto, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void updateItem_ShouldReturnResponseEntityWithOkStatus() {
        long itemId = 123L;
        long userId = 1L;
        UpdateItemDto updateItemDto = new UpdateItemDto();

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq("/" + itemId), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemClient.update(itemId, updateItemDto, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq("/" + itemId), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void findItemById_ShouldReturnResponseEntityWithOkStatus() {
        long itemId = 123L;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq("/" + itemId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemClient.findById(itemId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq("/" + itemId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void findAllItemsFromUser_ShouldReturnResponseEntityWithOkStatus() {
        long userId = 1L;

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemClient.allItemsFromUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void searchItems_ShouldReturnResponseEntityWithOkStatus() {
        String searchText = "item";

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq("/search"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemClient.search(searchText);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq("/search"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap());
    }

    @Test
    void saveComment_ShouldReturnResponseEntityWithOkStatus() {
        long userId = 1L;
        long itemId = 123L;
        Comment comment = new Comment();

        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq("/" + itemId + "/comment"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = itemClient.saveComment(userId, itemId, comment);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate).exchange(eq("/" + itemId + "/comment"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }
}