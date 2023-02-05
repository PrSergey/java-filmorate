package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({RuntimeException.class, NoSuchElementException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "такого обьекта нет")
    public Exception handleNoSuchElementException(Exception e) {
        return e;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "валидпция не прошла")
    public Exception handleValidationException(Exception e) {
        return e;
    }
}
