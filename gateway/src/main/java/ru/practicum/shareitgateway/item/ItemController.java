package ru.practicum.shareitgateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.item.dto.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid ItemDto itemDto,
                                     @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("save item {}, userId={}", itemDto, userId);
        return itemClient.save(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@PathVariable long itemId,
                                       @RequestBody UpdateItemDto itemDto,
                                       @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("update item {} itemId={} userId={}", itemDto, itemId, userId);
        return itemClient.update(itemId, itemDto, userId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findById(@PathVariable long itemId) {
        log.info("findById itemId={}", itemId);
        return itemClient.findById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> allItemsFromUser(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("allItemsFromUser userId={}", userId);
        return itemClient.allItemsFromUser(userId);
    }

    @GetMapping("search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("search text={}", text);
        return itemClient.search(text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                  @PathVariable long itemId,
                                                  @RequestBody @Valid Comment comment) {
        log.info("saveComment {} itemId={} userId={}", comment, itemId, userId);
        return itemClient.saveComment(userId, itemId, comment);
    }
}