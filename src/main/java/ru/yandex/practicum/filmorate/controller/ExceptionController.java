package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.webjars.NotFoundException;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler({NotFoundException.class, NoSuchElementException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "такого обьекта нет")
    public String handleNoSuchElementException(Exception e) {
        return "error = " + e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "валидация не прошла")
    public String handleValidationException(Exception e) {
        return "error = " + e.getMessage();
    }

}