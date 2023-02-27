package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    final static LocalDate BIRTH_OF_CINEMA = LocalDate.of(1895, 12, 28);
    private int id = 1;
    private Map<Long, Film> films = new HashMap<>();


    public List<Film> allFilms() {
        log.debug("Выдача всех фильмов из хранилища.");
        return new ArrayList<>(films.values());
    }


    public Film createFilm(Film film) throws ValidationException {
        log.debug("Создание фильма в хранилище.");
        if (validationFilm(film)) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }


    public Film updateFilm(Film film) throws ValidationException {
        log.debug("Обновление фильма в хранилище.");
        if (validationFilm(film)) {
            throw new ValidationException("Введены некорректные данные фильма, при обновление.");
        }
        if (!films.containsKey(film.getId())) {
            throw new ExistenceException("При обновлении, фильм не найден в базе.");
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
        return film;
    }

    public boolean validationFilm(Film film) {
        log.debug("Валидация даты релиза фильма.");
        return film.getReleaseDate().isBefore(BIRTH_OF_CINEMA);
    }

    public Map<Long, Film> getFilms() {
        return films;
    }
}
