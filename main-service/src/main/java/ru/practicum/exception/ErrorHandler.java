package ru.practicum.exception;

import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleBadRequest(BadRequestException e) {
        return Map.of(e.getMessage(), 400);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleNotFound(NotFoundException e) {
        return Map.of(e.getMessage(), 404);
    }

    @ExceptionHandler(value = UseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Integer> handleUseException(UseException e) {
        return Map.of(e.getMessage(), 409);
    }

    @ExceptionHandler(value = ContradictionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Integer> handleContradictionException(ContradictionException e) {
        return Map.of(e.getMessage(), 409);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Map.of(e.getMessage(), 400);
    }

    @ExceptionHandler(value = NonTransientDataAccessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Integer> handleNonTransientDataAccessException(NonTransientDataAccessException e) {
        return Map.of(e.getMessage(), 409);
    }
}
