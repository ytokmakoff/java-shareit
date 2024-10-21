package ru.practicum.shareitgateway.item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareitgateway.item.dto.*;

import static org.mockito.Mockito.*;

public class ItemControllerTest {

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldCallItemClientSave() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description 1");
        itemDto.setAvailable(true);

        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemClient.save(itemDto, userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemController.save(itemDto, userId);

        assertEquals(expectedResponse, actualResponse);
        verify(itemClient).save(itemDto, userId);
    }

    @Test
    void update_shouldCallItemClientUpdate() {
        long itemId = 1L;
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("Updated Item");
        updateItemDto.setDescription("Updated Description");
        updateItemDto.setAvailable(false);

        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemClient.update(itemId, updateItemDto, userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemController.update(itemId, updateItemDto, userId);

        assertEquals(expectedResponse, actualResponse);
        verify(itemClient).update(itemId, updateItemDto, userId);
    }

    @Test
    void findById_shouldCallItemClientFindById() {
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemClient.findById(itemId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemController.findById(itemId);

        assertEquals(expectedResponse, actualResponse);
        verify(itemClient).findById(itemId);
    }

    @Test
    void allItemsFromUser_shouldCallItemClientAllItemsFromUser() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemClient.allItemsFromUser(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemController.allItemsFromUser(userId);

        assertEquals(expectedResponse, actualResponse);
        verify(itemClient).allItemsFromUser(userId);
    }

    @Test
    void search_shouldCallItemClientSearch() {
        String text = "search text";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemClient.search(text)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemController.search(text);

        assertEquals(expectedResponse, actualResponse);
        verify(itemClient).search(text);
    }

    @Test
    void saveComment_shouldCallItemClientSaveComment() {
        long userId = 1L;
        long itemId = 1L;
        Comment comment = new Comment();
        comment.setText("Great item!");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(itemClient.saveComment(userId, itemId, comment)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemController.saveComment(userId, itemId, comment);

        assertEquals(expectedResponse, actualResponse);
        verify(itemClient).saveComment(userId, itemId, comment);
    }
}
