package ru.practicum.shareitserver.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.item.dto.*;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public Item save(@RequestBody ItemDto itemDto,
                     @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemController: create is called with userId = {}, itemDto = {}", userId, itemDto);
        Item item = itemService.save(itemDto, userId);
        log.info("ItemController: item created successfully with itemId = {}", item.getId());
        return item;
    }

    @PatchMapping("{itemId}")
    public Item update(@PathVariable long itemId,
                       @RequestBody UpdateItemDto itemDto,
                       @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemController: update is called with userId = {}, itemId = {}, update data = {}", userId, itemId, itemDto);
        Item item = itemService.update(itemId, itemDto, userId);
        log.info("ItemController: item updated successfully for itemId = {}", itemId);
        return item;
    }

    @GetMapping("{itemId}")
    public ItemWithCommentsDto findById(@PathVariable long itemId) {
        log.info("ItemController: findById is called for itemId = {}", itemId);
        ItemWithCommentsDto itemDto = itemService.findById(itemId);
        log.info("ItemController: item received successfully for itemId = {}", itemId);
        return itemDto;
    }

    @GetMapping
    public List<ItemWithBookingDateDto> allItemsFromUser(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemController: allItemsFromUser is called for userId = {}", userId);
        List<ItemWithBookingDateDto> items = itemService.allItemsFromUser(userId);
        log.info("ItemController: all items from userId = {} successfully received, total items = {}", userId, items.size());
        return items;
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("ItemController: search is called with text = {}", text);
        List<ItemDto> items = itemService.search(text);
        log.info("ItemController: search completed successfully, found items = {}", items.size());
        return items;
    }

    @PostMapping("{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody Comment comment) {
        log.info("ItemController: saveComment is called with userId = {}, itemId = {}, comment = {}", userId, itemId, comment);
        CommentDto savedComment = itemService.saveComment(comment, itemId, userId);
        log.info("ItemController: comment saved successfully with commentId = {}", savedComment.getId());
        return savedComment;
    }
}