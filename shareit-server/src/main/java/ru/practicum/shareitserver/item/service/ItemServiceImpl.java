package ru.practicum.shareitserver.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.item.ItemAccessDeniedException;
import ru.practicum.shareitserver.exception.item.ItemNotFoundException;
import ru.practicum.shareitserver.exception.item.ItemUnavailableException;
import ru.practicum.shareitserver.item.dto.*;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.CommentRepository;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Item save(ItemDto itemDto, long userId) {
        log.info("ItemServiceImpl: attempting to save item for user with id: {}", userId);

        User user = userService.findById(userId);
        log.info("ItemServiceImpl: found user with id: {}", userId);

        Item item = ItemMapper.dtoToItem(itemDto);
        log.info("ItemServiceImpl: mapping ItemDto to Item for user with id: {}", userId);

        item.setOwner(user);
        log.info("ItemServiceImpl: setting owner for item: {} to user with id: {}", item.getId(), userId);

        Item savedItem = itemRepository.save(item);
        log.info("ItemServiceImpl: successfully saved item with id: {}", savedItem.getId());

        return savedItem;
    }

    @Override
    @Transactional
    public Item update(long itemId, UpdateItemDto itemDto, long userId) {
        log.info("ItemServiceImpl: attempting to update item with id: {}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("ItemServiceImpl: item not found with id: {}", itemId);
                    return new ItemNotFoundException("Item with id " + itemId + " not found");
                });

        log.info("ItemServiceImpl: found item with id: {}", itemId);

        if (item.getOwner().getId() != userId) {
            log.warn("ItemServiceImpl: access denied for user with id: {} to item with id: {}", userId, itemId);
            throw new ItemAccessDeniedException("Access denied, you didn't create this item");
        }

        log.info("ItemServiceImpl: updating details for item with id: {}", itemId);
        updateItemDetails(item, itemDto);

        Item updatedItem = itemRepository.save(item);
        log.info("ItemServiceImpl: successfully updated item with id: {}", updatedItem.getId());

        return updatedItem;
    }

    private void updateItemDetails(Item item, UpdateItemDto itemDto) {
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithCommentsDto findById(long itemId) {
        log.info("ItemServiceImpl: attempting to find item with id: {}", itemId);

        ItemWithCommentsDto itemWithCommentsDto = itemRepository.findWithCommentsById(itemId)
                .orElseThrow(() -> {
                    log.warn("ItemServiceImpl: item not found with id: {}", itemId);
                    return new ItemNotFoundException("Item with id " + itemId + " not found");
                });

        log.info("ItemServiceImpl: found item with id: {}", itemId);

        log.info("ItemServiceImpl: retrieving comments for item with id: {}", itemId);
        itemWithCommentsDto.setComments(commentRepository.findByItemId(itemId));

        log.info("ItemServiceImpl: successfully retrieved item with id: {} and its comments", itemId);
        return itemWithCommentsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingDateDto> allItemsFromUser(long userId) {
        log.info("ItemServiceImpl: attempting to retrieve all items for user with id: {}", userId);

        userService.findById(userId);
        log.info("ItemServiceImpl: found user with id: {}", userId);

        List<ItemWithBookingDateDto> items = itemRepository.allItemsFromUser(userId);
        log.info("ItemServiceImpl: successfully retrieved {} items for user with id: {}", items.size(), userId);

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        log.info("ItemServiceImpl: attempting to search for items with text: {}", text);

        if (text.isBlank()) {
            log.info("ItemServiceImpl: search text is blank, returning empty list");
            return List.of();
        }

        List<ItemDto> items = itemRepository.search(text.toLowerCase());
        log.info("ItemServiceImpl: successfully found {} items for search text: {}", items.size(), text);

        return items;
    }

    @Override
    @Transactional
    public CommentDto saveComment(Comment comment, long itemId, long userId) {
        log.info("ItemServiceImpl: attempting to save comment for item with id: {} by user with id: {}", itemId, userId);

        User user = userService.findById(userId);
        log.info("ItemServiceImpl: found user with id: {}", userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("ItemServiceImpl: item not found with id: {}", itemId);
            return new ItemNotFoundException("Item with id " + itemId + " not found");
        });

        log.info("ItemServiceImpl: found item with id: {}", itemId);

        verifyBookingExists(itemId, userId);
        log.info("ItemServiceImpl: verified that booking exists for item with id: {} and user with id: {}", itemId, userId);

        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDate.now());

        CommentDto savedCommentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        log.info("ItemServiceImpl: successfully saved comment for item with id: {} by user with id: {}", itemId, userId);

        return savedCommentDto;
    }

    private void verifyBookingExists(long itemId, long userId) {
        bookingRepository.findByItemIdAndBookerIdAndEndBeforeNow(itemId, userId)
                .orElseThrow(() -> new ItemUnavailableException("Access denied: you didn't create a booking for this item"));
    }
}