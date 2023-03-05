package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Long addLike(Long filmId, Long userId);

    Long deleteLike(Long filmId, Long userId);

    List<Film> getTopFilmsOfLikes(Long countFilms);
    public List<Genre> getAllGenre();
    public Genre getGenreById(Long id);
    List<Mpa> getAllMpa();
    Mpa getMpaById(Long id);


}
