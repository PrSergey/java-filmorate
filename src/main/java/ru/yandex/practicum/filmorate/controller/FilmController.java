package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@RestController
public class FilmController {

   @Autowired
    private final FilmService filmsInMemory;


    public FilmController(FilmService filmsInMemory) {
        this.filmsInMemory=filmsInMemory;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmsInMemory.getFilms();
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmsInMemory.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmsInMemory.updateFilm(film);
    }
}
