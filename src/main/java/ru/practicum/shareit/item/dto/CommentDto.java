package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private Item item;
    private LocalDate created;
}
