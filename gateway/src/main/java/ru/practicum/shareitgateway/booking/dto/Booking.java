package ru.practicum.shareitgateway.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareitgateway.item.dto.Item;
import ru.practicum.shareitgateway.user.dto.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
