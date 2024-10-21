package ru.practicum.shareitgateway.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareitgateway.item.dto.Item;
import ru.practicum.shareitgateway.user.dto.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private User user;
    private LocalDateTime created;
    private List<Item> items;
}
