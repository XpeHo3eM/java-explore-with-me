package ru.practicum.main.handler;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
