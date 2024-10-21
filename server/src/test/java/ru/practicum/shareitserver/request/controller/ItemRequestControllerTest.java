package ru.practicum.shareitserver.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.service.ItemRequestService;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private MockMvc mvc;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();

        user = new User();
        user.setId(1);
        user.setEmail("user@email.con");
        user.setName("user name");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setUser(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description");
    }

    @Test
    void saveSuccessfully() throws Exception {
        when(itemRequestService
                .save(any(), anyLong()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest))
                        .header(X_SHARER_USER_ID, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    void findByUserIdSuccessfully() throws Exception {
        long userId = 42L;

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Request 1 description");
        request1.setUser(user);
        request1.setCreated(LocalDateTime.now());

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Request 2 description");
        request2.setUser(user);
        request2.setCreated(LocalDateTime.now());

        List<ItemRequestDto> itemRequests = List.of(request1, request2);

        when(itemRequestService.findByUserId(userId))
                .thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(itemRequests.size())))
                .andExpect(jsonPath("$[0].id", is(request1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request1.getDescription())))
                .andExpect(jsonPath("$[0].user.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].user.name", is(user.getName())));
    }

    @Test
    void findAllSuccessfully() throws Exception {
        long userId = 42L;

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Request 1 description");

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Request 2 description");

        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);

        when(itemRequestService.findAll(userId)).thenReturn(itemRequests);

        mvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(itemRequests.size())))
                .andExpect(jsonPath("$[0].id", is(itemRequest1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemRequest2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequest2.getDescription())));
    }

    @Test
    void findByIdSuccessfully() throws Exception {
        long requestId = 1L;

        User user = new User();
        user.setId(42L);
        user.setName("John Doe");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(requestId);
        itemRequestDto.setDescription("Request description");
        itemRequestDto.setUser(user);
        itemRequestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.findById(requestId)).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.user.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.user.name", is(user.getName())));
    }
}