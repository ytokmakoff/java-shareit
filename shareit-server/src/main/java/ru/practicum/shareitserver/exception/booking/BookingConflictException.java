package ru.practicum.shareitserver.exception.booking;

public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}