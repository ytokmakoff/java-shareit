package ru.practicum.shareitgateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;
import ru.practicum.shareitgateway.item.dto.*;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Item> save(ItemDto itemDto, long userId) {
        ResponseEntity<Object> response = post("", userId, itemDto);
        Item item = (Item) response.getBody();
        return new ResponseEntity<>(item, response.getStatusCode());
    }

    public ResponseEntity<Item> update(long itemId, UpdateItemDto itemDto, long userId) {
        ResponseEntity<Object> response = patch("/" + itemId, userId, itemDto);
        Item item = (Item) response.getBody();
        return new ResponseEntity<>(item, response.getStatusCode());
    }

    public ResponseEntity<ItemWithCommentsDto> findById(long itemId) {
        ResponseEntity<Object> response = get("/" + itemId);
        ItemWithCommentsDto itemWithCommentsDto = (ItemWithCommentsDto) response.getBody();
        return new ResponseEntity<>(itemWithCommentsDto, response.getStatusCode());
    }

    public ResponseEntity<ItemWithBookingDateDto> allItemsFromUser(long userId) {
        ResponseEntity<Object> response = get("", userId);
        ItemWithBookingDateDto itemWithBookingDateDto = (ItemWithBookingDateDto) response.getBody();
        return new ResponseEntity<>(itemWithBookingDateDto, response.getStatusCode());
    }

    public ResponseEntity<ItemDto> search(String text) {
        Map<String, Object> params = Map.of(
                "text", text
        );
        ResponseEntity<Object> response = get("/search", null, params);
        ItemDto itemDto = (ItemDto) response.getBody();
        return new ResponseEntity<>(itemDto, response.getStatusCode());
    }

    public ResponseEntity<CommentDto> saveComment(long userId, long itemId, Comment comment) {
        ResponseEntity<Object> response = post("/" + itemId + "/comment", userId, comment);
        CommentDto commentDto = (CommentDto) response.getBody();
        return new ResponseEntity<>(commentDto, response.getStatusCode());
    }
}