package ru.practicum.shareitserver.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.exception.request.ItemRequestNotFoundException;
import ru.practicum.shareitserver.exception.user.UserNotFoundException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestMapper;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequest save(ItemRequest itemRequest, long userId) {
        log.info("ItemRequestService: save is called by userId={}", userId);

        LocalDateTime now = LocalDateTime.now();
        itemRequest.setCreated(now);
        log.debug("ItemRequestService: Setting created time to {}", now);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("ItemRequestService: User with id {} not found", userId);
                    return new UserNotFoundException("User with id " + userId + " not found");
                });

        itemRequest.setUser(user);
        log.debug("ItemRequestService: User set for itemRequest: {}", user);

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.info("ItemRequestService: ItemRequest saved successfully with id={}", savedItemRequest.getId());

        return savedItemRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findByUserId(long userId) {
        log.info("ItemRequestService: findByUserId is called by userId={}", userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByUserId(userId);
        log.info("ItemRequestService: Found {} item requests for userId={}", itemRequests.size(), userId);

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        if (requestIds.isEmpty()) {
            log.info("ItemRequestService: No item requests found for userId={}", userId);
            return itemRequests.stream()
                    .map(ItemRequestMapper::toDto)
                    .collect(Collectors.toList());
        }

        Map<Long, List<Item>> itemsByRequestId = itemRequestRepository.findItemsByRequestIds(requestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        log.info("ItemRequestService: Successfully retrieved items for {} item requests", requestIds.size());

        List<ItemRequestDto> itemRequestDtos = itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto dto = ItemRequestMapper.toDto(itemRequest);
                    dto.setItems(itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList()));
                    return dto;
                })
                .toList();

        log.info("ItemRequestService: Successfully converted item requests to DTOs for userId={}", userId);

        return itemRequestDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequest> findAll(long userId) {
        log.info("ItemRequestService: findAll is called by userId={}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("ItemRequestService: User with id {} not found", userId);
            throw new UserNotFoundException("User with id " + userId + " not found");
        }

        log.debug("ItemRequestService: User with id {} found, proceeding to fetch item requests", userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdNot(userId);

        log.info("ItemRequestService: Found {} item requests for users other than userId={}", itemRequests.size(), userId);

        return itemRequests;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto findById(long requestId) {
        log.info("ItemRequestService: findById is called with requestId={}", requestId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Item request with id " + requestId + " not found"));

        log.debug("ItemRequestService: Found item request: {}", itemRequest);

        List<Item> items = itemRequestRepository.findItemsByRequestId(requestId);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
        itemRequestDto.setItems(items);

        log.info("ItemRequestService: Successfully converted item request to DTO for requestId={}", requestId);

        return itemRequestDto;
    }
}