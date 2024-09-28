package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking save(Long userId, BookingSaveDto bookingSaveDto);

    Booking responseToRequest(long userId, long bookingId, boolean approved);

    BookingDto findBookingById(long userId, long bookingId);

    List<Booking> findAllForUser(long userId, BookingState state);

    List<Booking> findReservations(long userId, BookingState state);
}
