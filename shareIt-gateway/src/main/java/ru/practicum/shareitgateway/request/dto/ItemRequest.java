package ru.practicum.shareitgateway.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareitgateway.user.dto.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequest {
    private long id;
    @NotBlank
    private String description;
    private User user;
    private LocalDateTime created;
}
