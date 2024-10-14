package ru.practicum.shareitserver.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorDetails {
    private String message;
    private String details;
}