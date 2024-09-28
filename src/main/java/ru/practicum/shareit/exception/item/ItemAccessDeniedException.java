package ru.practicum.shareit.exception.item;

public class ItemAccessDeniedException extends RuntimeException {
    public ItemAccessDeniedException(String message) {
        super(message);
    }
}
