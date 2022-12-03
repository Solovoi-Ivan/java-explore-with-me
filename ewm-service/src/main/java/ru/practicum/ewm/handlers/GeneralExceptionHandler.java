package ru.practicum.ewm.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exceptions.UnsupportedStateException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.util.JsonConstants;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestControllerAdvice
public class GeneralExceptionHandler {


    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleFailedValidation(final ValidationException e) {
        return Map.of("status", HttpStatus.BAD_REQUEST.toString(),
                "reason", "Validation error",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(JsonConstants.pattern)));
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRuntimeException(final RuntimeException e) {
        return Map.of("status", HttpStatus.BAD_REQUEST.toString(),
                "reason", "Internal Server error",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(JsonConstants.pattern)));
    }

    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEntityNotFoundValidation(final EntityNotFoundException e) {
        return Map.of("status", HttpStatus.NOT_FOUND.toString(),
                "reason", "Entity Not Found error",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(JsonConstants.pattern)));
    }

    @ExceptionHandler({UnsupportedStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnsupportedState(final UnsupportedStateException e) {
        return Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "reason", "Unsupported State error",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(JsonConstants.pattern)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDbException(final SQLException e) {
        return Map.of("status", HttpStatus.CONFLICT.toString(),
                "reason", "Database error",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(JsonConstants.pattern)));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(final IllegalArgumentException e) {
        return Map.of("status", HttpStatus.BAD_REQUEST.toString(),
                "reason", "Illegal Argument error",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(JsonConstants.pattern)));
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNullPointerException(final NullPointerException e) {
        return Map.of("status", HttpStatus.BAD_REQUEST.toString(),
                "reason", "Null Pointer Exception error",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(JsonConstants.pattern)));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return Map.of("status", HttpStatus.BAD_REQUEST.toString(),
                "reason", "Argument Not Valid error",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(JsonConstants.pattern)));
    }
}