package ru.practicum.ewm.exceptions;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException(final String message) {
        super(message);
    }
}
