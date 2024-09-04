package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Optional<Item> getById(long itemId);

    Item update(long itemId, Item item);

    List<ItemDto> allItemsFromUser(long userId);

    List<ItemDto> search(String text);
}
