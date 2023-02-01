package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {

   @Autowired
    private final FilmStorage filmsInMemory;
    public FilmController(FilmStorage filmsInMemory) {
        this.filmsInMemory=filmsInMemory;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmsInMemory.allFilms();
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmsInMemory.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmsInMemory.updateFilm(film);
    }
}
