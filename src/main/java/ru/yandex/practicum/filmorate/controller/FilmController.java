package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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


    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmsInMemory.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, ExistenceException {
        return filmsInMemory.updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) throws ValidationException {
        if (id < 0) {
            throw new ValidationException("Id не может быть отрицательным.");
        }
        return filmsInMemory.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Long addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmsInMemory.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Long deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmsInMemory.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Long count) {
        return filmsInMemory.getTopFilmsOfLikes(Objects.requireNonNullElse(count, 10L));
    }

}
