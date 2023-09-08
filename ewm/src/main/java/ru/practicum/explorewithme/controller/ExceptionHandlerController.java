package ru.practicum.explorewithme.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.exception.ApiErrorMessage;
import ru.practicum.explorewithme.exception.IllegalEventStateException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ParticipationRequestException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = {IllegalEventStateException.class, ParticipationRequestException.class})
    public ResponseEntity<ApiErrorMessage> handleValidateException(Exception exception) {
        log.debug("Получен код 409 Conflict [{}]", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorMessage(exception.getMessage()));
    }
    @ExceptionHandler(value = {IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorMessage> handleException(Exception exception) {
        log.debug("Получен статус 400 Bad request [{}]", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ApiErrorMessage> handleNotFoundException(Exception exception) {
        log.debug("Получен статус 404 Not found [{}]", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ApiErrorMessage> handleConstraintViolationException(Exception exception) {
        log.debug("Получен код 409 Conflict [{}]", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorMessage("Ошибка валидации"));
    }
}
