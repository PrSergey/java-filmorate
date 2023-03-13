package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class FilmController {

    @Qualifier("dbFilmStorage")
    private final FilmService filmService;

    @Autowired

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) throws ValidationException {
        if (id < 0) {
            throw new ExistenceException("Id не может быть отрицательным.");
        }
        return filmService.getFilmById(id);
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.debug("Создание фильма в контроллере");
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, ExistenceException {
        return filmService.updateFilm(film);
    }


    @PutMapping("/films/{id}/like/{userId}")
    public Long addLike(@PathVariable Long id, @PathVariable Long userId) {
        if (id < 0 || userId < 0) {
            throw new ExistenceException("Id не может быть отрицательным");
        }
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Long deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        if (id < 0 || userId < 0) {
            throw new ExistenceException("Id не может быть отрицательным");
        }
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Long count) {
        return filmService.getTopFilmsOfLikes(Objects.requireNonNullElse(count, 10L));
    }




}
