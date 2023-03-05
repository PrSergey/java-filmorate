package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;


public interface FilmStorage {
    List<Film> getAllFilms();
    Film getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    boolean validationFilm(Film film);

    Map<Long, Film> getFilms();

    List<Genre> getAllGenre();
    List<Mpa> getAllMpa();
    boolean addLikeByFilm(Long filmId, Long userId);
    boolean deleteLikeByFilm(Long filmId, Long userId);
}
