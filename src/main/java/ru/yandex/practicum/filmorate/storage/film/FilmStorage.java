package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    List<Film> getAllFilms();
    Film getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    boolean validationFilm(Film film);

    Map<Long, Film> getFilms();

    void addLikeByFilm(Long filmId, Long userId);

    boolean deleteLikeByFilm(Long filmId, Long userId);

    List<Film> getTopFilm(Long countFilm);
}