package ru.practicum.shareitserver.exception.item;

public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(String message) {
        super(message);
    }
}