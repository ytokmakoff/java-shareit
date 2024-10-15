package ru.practicum.shareitgateway.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareitgateway.user.dto.User;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private long id;
    @NotBlank
    private String text;

    private Item item;

    private User user;

    private LocalDate created;
}
