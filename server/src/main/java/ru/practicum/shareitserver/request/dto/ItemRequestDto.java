package ru.practicum.shareitserver.request.dto;

import lombok.Data;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private long id;
    private String description;
    private User user;
    private LocalDateTime created;
    private List<Item> items;
}