package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmService {

    List<Film> getAllFilms();
    Film getFilmById(Long id);
    Film getFilm(Long id);
    Film createFilm(Film film);
    Film updateFilm(Film film);
    Long addLike (Long filmId, Long userId);
    Long deleteLike (Long filmId, Long userId);
    List getTopFilmsOfLikes (Long countFilms);

}
