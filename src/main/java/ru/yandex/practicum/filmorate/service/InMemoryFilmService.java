package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InMemoryFilmService implements FilmService {

    FilmStorage filmInMemory;

    @Autowired
    public InMemoryFilmService(FilmStorage filmInMemory) {
        this.filmInMemory = filmInMemory;
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Выдача всех фильмов из сервиса.");
        return filmInMemory.allFilms();
    }


    @Override
    public Film getFilmById(Long id) throws ValidationException {
        log.debug("Выдача фильма из сервиса.");
        if (!filmInMemory.getFilms().containsKey(id)) {
            throw new ExistenceException("Данного фильма нет в базе.");
        }
        return filmInMemory.getFilms().get(id);
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Создание фильма в сервисе.");
        return filmInMemory.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Обновление фильма в сервисе.");
        return filmInMemory.updateFilm(film);
    }

    @Override
    public Long addLike(Long filmId, Long userId) throws ValidationException {
        log.debug("Добавление лайка к фильму в сервисе.");
        getFilmLikes(filmId).add(userId);
        return userId;
    }

    @Override
    public Long deleteLike(Long filmId, Long userId) throws ValidationException {
        log.debug("Удаление лайка к фильму в сервисе.");
        if (!getFilmLikes(filmId).contains(userId)) {
            throw new ExistenceException("У фильма с id " + filmId + " нет лайка от пользователся с id " + userId);
        }
        getFilmLikes(filmId).remove(userId);
        return userId;
    }

    @Override
    public List<Film> getTopFilmsOfLikes(Long countFilms) {
        log.debug("Выдача фильма по топу из сервиса.");
        List<Film> allFilms = new ArrayList<>(filmInMemory.getFilms().values());
        for (Film film : filmInMemory.getFilms().values()) {
            if (film.getLikes().size() > 0) {
                allFilms.add(film);
            }
        }
        allFilms.sort(Comparator.comparingInt(film -> ((Film) film).getLikes().size()).reversed());
        return allFilms.stream().limit(countFilms).collect(Collectors.toList());
    }

    public HashSet<Long> getFilmLikes(Long filmId) throws ValidationException {
        if (!filmInMemory.getFilms().containsKey(filmId)) {
            throw new ExistenceException("Фильм с данным Id не найден.");
        } else {
            return filmInMemory.getFilms().get(filmId).getLikes();
        }
    }

}
