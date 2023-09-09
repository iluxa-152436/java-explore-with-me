package ru.practicum.explorewithme.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = {IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorMessage> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorMessage(exception.getMessage()));
    }
}
