package ru.practicum.shareitgateway.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.request.dto.ItemRequest;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid ItemRequest itemRequest,
                                       @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("save itemRequest {} userId={}", itemRequest, userId);
        return itemRequestClient.save(itemRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("findByUserId userId={}", userId);
        return itemRequestClient.findByUserId(userId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> findAll(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("findAll userId={}", userId);
        return itemRequestClient.findAll(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findById(@PathVariable("requestId") long requestId) {
        log.info("findById requestId={}", requestId);
        return itemRequestClient.findById(requestId);
    }
}