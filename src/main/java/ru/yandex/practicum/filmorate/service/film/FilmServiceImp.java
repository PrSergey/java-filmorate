package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmServiceImp implements FilmService {

    FilmStorage filmStorage;

    @Autowired
    public FilmServiceImp(@Qualifier("dbFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Выдача всех фильмов из сервиса.");
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) throws ValidationException {
        log.debug("Выдача фильма из сервиса.");
        return filmStorage.getFilmById(id);
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Создание фильма в сервисе.");
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Обновление фильма в сервисе.");
        return filmStorage.updateFilm(film);
    }

    @Override
    public Long addLike(Long filmId, Long userId) throws ValidationException {
        log.debug("Добавление лайка к фильму в сервисе.");
        filmStorage.addLikeByFilm(filmId, userId);
        return userId;
    }

    @Override
    public Long deleteLike(Long filmId, Long userId) throws ValidationException {
        log.debug("Удаление лайка к фильму в сервисе.");
        if (!filmStorage.deleteLikeByFilm(filmId, userId)) {
            throw new ExistenceException("У фильма с id " + filmId + " нет лайка от пользователся с id " + userId);
        }
        return userId;
    }

    @Override
    public List<Film> getTopFilmsOfLikes(Long countFilms) {
        log.debug("Выдача фильма по топу из сервиса.");
        return filmStorage.getTopFilm(countFilms);
    }


}
