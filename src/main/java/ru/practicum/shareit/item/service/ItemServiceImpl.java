package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.item.ItemAccessDeniedException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.item.ItemUnavailableException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item save(ItemDto itemDto, long userId) {
        User user = userService.findById(userId);

        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(user);

        return itemRepository.save(item);
    }

    @Override
    public Item update(long itemId, UpdateItemDto itemDto, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));

        if (item.getOwner().getId() != userId) {
            throw new ItemAccessDeniedException("Access denied you didn't create this item");
        }

        updateItemDetails(item, itemDto);

        return itemRepository.save(item);
    }

    private void updateItemDetails(Item item, UpdateItemDto itemDto) {
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
    }

    @Override
    public ItemWithCommentsDto findById(long itemId) {
        ItemWithCommentsDto itemWithCommentsDto = itemRepository.findWithCommentsById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));

        itemWithCommentsDto.setComments(commentRepository.findByItemId(itemId));

        return itemWithCommentsDto;
    }

    @Override
    public List<ItemWithBookingDateDto> allItemsFromUser(long userId) {
        userService.findById(userId);
        return itemRepository.allItemsFromUser(userId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text.toLowerCase());
    }

    @Override
    public CommentDto saveComment(Comment comment, long itemId, long userId) {
        User user = userService.findById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Item with id " + itemId + " not found"));

        verifyBookingExists(itemId, userId);

        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDate.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void verifyBookingExists(long itemId, long userId) {
        bookingRepository.findByItemIdAndBookerIdAndEndBeforeNow(itemId, userId)
                .orElseThrow(() -> new ItemUnavailableException("Access denied: you didn't create a booking for this item"));
    }
}