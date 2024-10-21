package ru.practicum.shareitserver.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareitserver.item.dto.*;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ObjectMapper mapper = new ObjectMapper();
    User owner;
    Item item;
    ItemDto itemDto;
    UpdateItemDto updateItemDto;
    Comment comment;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@email.com");

        item = new Item();
        item.setId(1);
        item.setName("item name");
        item.setDescription("item description");
        item.setAvailable(true);
        item.setOwner(owner);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setUser(owner);

        updateItemDto = new UpdateItemDto();
        updateItemDto.setDescription("new description");

        itemDto = new ItemDto(1, "item name", "item description", true, 1);
    }


    @Test
    void saveShouldSaveItemSuccessfully() throws Exception {
        when(itemService.save(any(), anyLong()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(X_SHARER_USER_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(owner.getId()), Long.class));
    }

    @Test
    void updateItemSuccessfully() throws Exception {
        long itemId = 1L;
        long userId = owner.getId();

        when(itemService.update(anyLong(), any(UpdateItemDto.class), anyLong()))
                .thenReturn(item);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(owner.getId()), Long.class));
    }

    @Test
    void findByIdRetrieveItemByIdSuccessfully() throws Exception {
        long itemId = 1L;

        ItemWithCommentsDto itemWithCommentsDto = new ItemWithCommentsDto(
                itemId,
                "Item Name",
                "Item Description",
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                List.of(comment)

        );

        when(itemService.findById(itemId)).thenReturn(itemWithCommentsDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithCommentsDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithCommentsDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithCommentsDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithCommentsDto.getAvailable())))
                .andExpect(jsonPath("$.comments[0].id", is(comment.getId()), Long.class));
    }

    @Test
    void findAllRetrieveAllItemsFromUserSuccessfully() throws Exception {
        long userId = 42L;

        ItemWithBookingDateDto item1 = new ItemWithBookingDateDto(1L, "Item 1", "Description 1", true, LocalDateTime.of(2024, 10, 1, 10, 0), LocalDateTime.of(2024, 11, 1, 10, 0));
        ItemWithBookingDateDto item2 = new ItemWithBookingDateDto(2L, "Item 2", "Description 2", false, LocalDateTime.of(2024, 9, 1, 10, 0), LocalDateTime.of(2024, 12, 1, 10, 0));
        List<ItemWithBookingDateDto> items = List.of(item1, item2);

        when(itemService.allItemsFromUser(userId)).thenReturn(items);

        mvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(items.size())))
                .andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description", is(item1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item1.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(item1.getLastBooking().toString())))
                .andExpect(jsonPath("$[0].nextBooking", is(item1.getNextBooking().toString())))
                .andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(item2.getName())))
                .andExpect(jsonPath("$[1].description", is(item2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(item2.getAvailable())))
                .andExpect(jsonPath("$[1].lastBooking", is(item2.getLastBooking().toString())))
                .andExpect(jsonPath("$[1].nextBooking", is(item2.getNextBooking().toString())));
    }

    @Test
    void searchItemsByTextSuccessfully() throws Exception {
        String searchText = "item";

        // Создание данных для теста
        ItemDto item1 = new ItemDto(1L, "Item 1", "Description 1", true, 1);
        ItemDto item2 = new ItemDto(2L, "Item 2", "Description 2", true, 1);
        List<ItemDto> items = List.of(item1, item2);

        when(itemService.search(searchText)).thenReturn(items);

        mvc.perform(get("/items/search")
                        .param("text", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(items.size())))
                .andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description", is(item1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item1.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(item2.getName())))
                .andExpect(jsonPath("$[1].description", is(item2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(item2.getAvailable())));
    }

    @Test
    void saveCommentForItemSuccessfully() throws Exception {
        long userId = 42L;
        long itemId = 1L;

        CommentDto savedCommentDto = new CommentDto(1L, "Great item!", "John Doe", item, LocalDate.now());

        when(itemService.saveComment(any(Comment.class), eq(itemId), eq(userId)))
                .thenReturn(savedCommentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(X_SHARER_USER_ID, userId)
                        .content(mapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(savedCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(savedCommentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(savedCommentDto.getCreated().toString())));
    }
}