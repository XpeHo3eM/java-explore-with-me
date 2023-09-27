package ru.practicum.main.handler;

public class ValidateException extends RuntimeException {
    public ValidateException(String message) {
        super(message);
    }
}