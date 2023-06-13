package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(ValidationException e) {
        log.warn("ValidationException", e);
        return new ErrorResponse("400 Bad Request", e.getMessage());
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleObjectNotFoundException(NotFoundException e) {
        log.warn("NotFoundException", e);
        return new ErrorResponse("404 Not Found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("Throwable", e);
        return new ErrorResponse("500 Internal Server Error", "Непредвиденная ошибка.");
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.warn("ConflictException", e);
        return new ErrorResponse("404 Conflict", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException ", e);
        return new ErrorResponse("404 MethodArgumentNotValidException", e.getMessage());
    }
}
