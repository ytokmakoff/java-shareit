package ru.practicum.shareitserver.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest save(@RequestBody ItemRequest itemRequest,
                            @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemRequestController: save is called by userId={}", userId);
        ItemRequest savedItemRequest = itemRequestService.save(itemRequest, userId);
        log.info("ItemRequestController: ItemRequest saved successfully for userId={}", userId);
        return savedItemRequest;
    }

    @GetMapping
    public List<ItemRequestDto> findByUserId(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemRequestController: findByUserId is called by userId={}", userId);
        List<ItemRequestDto> itemRequests = itemRequestService.findByUserId(userId);
        log.info("ItemRequestController: Successfully retrieved {} item requests for userId={}", itemRequests.size(), userId);
        return itemRequests;
    }

    @GetMapping("all")
    public List<ItemRequest> findAll(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemRequestController: findAll is called by userId={}", userId);
        List<ItemRequest> itemRequests = itemRequestService.findAll(userId);
        log.info("ItemRequestController: Successfully retrieved {} item requests for userId={}", itemRequests.size(), userId);
        return itemRequests;
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findById(@PathVariable("requestId") long requestId) {
        log.info("ItemRequestController: findById is called with requestId={}", requestId);
        ItemRequestDto itemRequestDto = itemRequestService.findById(requestId);
        log.info("ItemRequestController: Successfully retrieved item request with requestId={}", requestId);
        return itemRequestDto;
    }
}