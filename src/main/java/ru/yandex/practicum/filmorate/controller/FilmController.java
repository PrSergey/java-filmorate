package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class FilmController {


    private final FilmService filmsInMemory;

    @Autowired
    public FilmController(FilmService filmsInMemory) {
        this.filmsInMemory = filmsInMemory;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmsInMemory.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) throws ValidationException {
        if (id < 0) {
            throw new ExistenceException("Id не может быть отрицательным.");
        }
        return filmsInMemory.getFilmById(id);
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.debug("Создание фильма в контроллере");
        return filmsInMemory.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, ExistenceException {
        return filmsInMemory.updateFilm(film);
    }


    @PutMapping("/films/{id}/like/{userId}")
    public Long addLike(@PathVariable Long id, @PathVariable Long userId) {
        if (id < 0 || userId < 0) {
            throw new ExistenceException("Id не может быть отрицательным");
        }
        return filmsInMemory.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Long deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        if (id < 0 || userId < 0) {
            throw new ExistenceException("Id не может быть отрицательным");
        }
        return filmsInMemory.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Long count) {
        return filmsInMemory.getTopFilmsOfLikes(Objects.requireNonNullElse(count, 10L));
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenre() {
        return filmsInMemory.getAllGenre();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        if (getAllGenre().size() < id || id < 0) {
            throw new ExistenceException("Данного жанра не существует.");
        }
        return filmsInMemory.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        return filmsInMemory.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        if (getAllMpa().size() < id || id < 0) {
            throw new ExistenceException("Данного жанра не существует.");
        }
        return filmsInMemory.getMpaById(id);
    }
}
