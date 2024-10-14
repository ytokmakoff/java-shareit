package ru.practicum.shareitgateway.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareitgateway.user.dto.User;

public class Item {
    private long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private long requestId;
}
