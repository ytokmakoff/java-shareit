package ru.practicum.shareit.exception.booking;

public class BookingAccessDeniedException extends RuntimeException {
    public BookingAccessDeniedException(String message) {
        super(message);
    }
}
