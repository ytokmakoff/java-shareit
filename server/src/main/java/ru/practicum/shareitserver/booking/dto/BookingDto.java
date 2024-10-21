package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
