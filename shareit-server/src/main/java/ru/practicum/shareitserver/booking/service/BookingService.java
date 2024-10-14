package ru.practicum.shareitserver.booking.service;

import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.dto.BookingSaveDto;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking save(Long userId, BookingSaveDto bookingSaveDto);

    Booking responseToRequest(long userId, long bookingId, boolean approved);

    BookingDto findBookingById(long userId, long bookingId);

    List<Booking> findAllForUser(long userId, BookingState state);

    List<Booking> findReservations(long userId, BookingState state);
}
