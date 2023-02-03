package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.*;
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    final static LocalDate BIRTH_OF_CINEMA = LocalDate.of(1895, 12, 28);
    private int id = 1;



    private Map<Long, Film> films = new HashMap<>();



    public List<Film> allFilms() {
        return new ArrayList<>(films.values());
    }


    public Film createFilm(Film film) throws ValidationException {
        if (validationFilm(film)) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }
        film.setId(id++);
        films.put(film.getId(),film);
        return film;
    }


    public Film updateFilm(Film film) throws ValidationException {
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

    public Map<Long, Film> getFilms() {
        return films;
    }
}
