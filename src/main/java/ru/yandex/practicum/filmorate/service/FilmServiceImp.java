package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmServiceImp implements FilmService {

    FilmStorage filmInMemory;

    @Autowired
    public FilmServiceImp(@Qualifier("dbFilmStorage") FilmStorage filmStorage) {
        this.filmInMemory = filmStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Выдача всех фильмов из сервиса.");
        return filmInMemory.getAllFilms();
    }


    @Override
    public Film getFilmById(Long id) throws ValidationException {
        log.debug("Выдача фильма из сервиса.");
        return filmInMemory.getFilmById(id);
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Создание фильма в сервисе.");
        film.setLikes(new HashSet<>());
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
        filmInMemory.addLikeByFilm(filmId, userId);
        return userId;
    }

    @Override
    public Long deleteLike(Long filmId, Long userId) throws ValidationException {
        log.debug("Удаление лайка к фильму в сервисе.");
        if (!getFilmLikes(filmId).contains(userId)) {
            throw new ExistenceException("У фильма с id " + filmId + " нет лайка от пользователся с id " + userId);
        }
        filmInMemory.deleteLikeByFilm(filmId, userId);
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

    @Override
    public List<Genre> getAllGenre() {
        log.debug("Выдача всех жанров.");
        return filmInMemory.getAllGenre();
    }

    @Override
    public Genre getGenreById(Long id) {
        log.debug("Выдача жанра по id.");
        for (Genre genre : getAllGenre()) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        return null;
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.debug("Выдача всех возрастных ограничений.");
        return filmInMemory.getAllMpa();
    }

    @Override
    public Mpa getMpaById(Long id) {
        log.debug("Выдача возрастных ограничений по id.");
        for (Mpa mpa : getAllMpa()) {
            if (mpa.getId() == id) {
                return mpa;
            }
        }
        return null;
    }

    public HashSet<Long> getFilmLikes(Long filmId) throws ValidationException {
        log.debug("Выдача лайков фильма.");
        if (!filmInMemory.getFilms().containsKey(filmId)) {
            throw new ExistenceException("Фильм с данным Id не найден.");
        } else {
            return filmInMemory.getFilms().get(filmId).getLikes();
        }
    }

}
