package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Kaknibut {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity as () {
        return ResponseEntity.ok().build();
    }
}
