package ru.practicum.shareitgateway.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareitgateway.user.dto.User;

@Getter
@Setter
@AllArgsConstructor
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
