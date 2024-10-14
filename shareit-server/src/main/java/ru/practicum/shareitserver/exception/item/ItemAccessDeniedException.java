package ru.practicum.shareitserver.exception.item;

public class ItemAccessDeniedException extends RuntimeException {
    public ItemAccessDeniedException(String message) {
        super(message);
    }
}
