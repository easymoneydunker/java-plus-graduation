package ru.practicum.common;

public class ConflictException extends RuntimeException {
    public ConflictException(final String message) {
        super(message);
    }
}
