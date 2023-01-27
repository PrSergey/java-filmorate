package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    final static LocalDate BIRTH_OF_CINEMA = LocalDate.of(1895, 12, 28);
    private int id = 1;
    private Map<Long, Film> films = new HashMap<>();


    @GetMapping("/films")
    public List<Film> allFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (validationFilm(film)) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }
        film.setId(id);
        films.put(film.getId(),film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (validationFilm(film) ) {
            throw new ValidationException("Введены некорректные данные фильма, при обновление.");
        }
        if (!films.containsKey(film.getId())){
            throw new ValidationException("При обновлении, фильм не найден в базе.");
        }
        films.put(film.getId(),film);
        return film;
    }

    public boolean validationFilm(Film film) {
        return film.getReleaseDate().isBefore(BIRTH_OF_CINEMA);
    }
}
