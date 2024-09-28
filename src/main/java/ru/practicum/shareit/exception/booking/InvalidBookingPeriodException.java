package ru.practicum.shareit.exception.booking;

public class InvalidBookingPeriodException extends RuntimeException {
    public InvalidBookingPeriodException(String message) {
        super(message);
    }
}