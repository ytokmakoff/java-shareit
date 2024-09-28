package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getStart(), booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getStatus());
    }
}
