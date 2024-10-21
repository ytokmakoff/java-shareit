package ru.practicum.shareitgateway.request;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareitgateway.request.dto.ItemRequest;

import static org.mockito.Mockito.*;

public class ItemRequestControllerTest {

    @Mock
    private ItemRequestClient itemRequestClient;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldCallItemRequestClientSave() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Need an item");

        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemRequestClient.save(itemRequest, userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestController.save(itemRequest, userId);

        assertEquals(expectedResponse, actualResponse);
        verify(itemRequestClient).save(itemRequest, userId);
    }

    @Test
    void findByUserId_shouldCallItemRequestClientFindByUserId() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemRequestClient.findByUserId(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestController.findByUserId(userId);

        assertEquals(expectedResponse, actualResponse);
        verify(itemRequestClient).findByUserId(userId);
    }

    @Test
    void findAll_shouldCallItemRequestClientFindAll() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemRequestClient.findAll(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestController.findAll(userId);

        assertEquals(expectedResponse, actualResponse);
        verify(itemRequestClient).findAll(userId);
    }

    @Test
    void findById_shouldCallItemRequestClientFindById() {
        long requestId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemRequestClient.findById(requestId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestController.findById(requestId);

        assertEquals(expectedResponse, actualResponse);
        verify(itemRequestClient).findById(requestId);
    }
}