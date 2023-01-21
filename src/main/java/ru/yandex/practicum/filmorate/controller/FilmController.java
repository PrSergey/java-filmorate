package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    final static LocalDate BIRTH_OF_CINEMA = LocalDate.of(1895,12,28);
    private int id=1;
    private List<Film> films = new ArrayList<>();


    @GetMapping ("/films")
    public List <Film> allFilms (){
        log.debug("Получен запрос GET /films.");
        return films;
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.debug("Получен запрос POST /films.");
        if (checkFilm(film)) {
            throw new ValidationException();
        }
        film.setId(id++);
        films.add((film.getId()-1), film);
        return film;
    }

    @PutMapping ("/films")
    public Film updateFilm (@Valid @RequestBody Film film) throws ValidationException {
        log.debug("Получен запрос PUT /films.");
        if (checkFilm(film) || (films.size() < film.getId())){
            throw new ValidationException();
        }
        films.set((film.getId()-1), film);
        return  film;
    }

    public boolean checkFilm (Film film) {
        return  film.getDuration().toNanos()<0 || film.getReleaseDate().isBefore(BIRTH_OF_CINEMA);
    }
}
