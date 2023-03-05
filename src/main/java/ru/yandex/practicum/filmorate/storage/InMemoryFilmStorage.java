package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;
@Data
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {



    final static LocalDate BIRTH_OF_CINEMA = LocalDate.of(1895, 12, 28);
    private int id = 1;
    private Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        log.debug("Выдача всех фильмов из хранилища.");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new ExistenceException("Данного фильма нет в базе.");
        }
        return films.get(id);
    }

    @Override
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

    @Override
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
    @Override
    public boolean validationFilm(Film film) {
        log.debug("Валидация даты релиза фильма.");
        return film.getReleaseDate().isBefore(BIRTH_OF_CINEMA);
    }

    @Override
    public List<Mpa> getAllMpa() {
        return null;
    }

    @Override
    public boolean deleteLikeByFilm(Long filmId, Long userId) {
        return getFilmById(filmId).getLikes().remove(userId);
    }

    @Override
    public boolean addLikeByFilm(Long filmId, Long userId) {
        return getFilmById(filmId).getLikes().add(userId);
    }

    @Override
    public List<Genre> getAllGenre() {
        return null;
    }
    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }
}
