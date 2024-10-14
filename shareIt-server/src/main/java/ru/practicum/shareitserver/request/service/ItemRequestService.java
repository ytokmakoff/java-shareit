package ru.practicum.shareitserver.request.service;

import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest save(ItemRequest itemRequest, long userId);

    List<ItemRequestDto> findByUserId(long userId);

    List<ItemRequest> findAll(long userId);

    ItemRequestDto findById(long requestId);
}
