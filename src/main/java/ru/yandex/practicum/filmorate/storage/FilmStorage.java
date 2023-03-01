package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;


public interface FilmStorage {
    List<Film> allFilms();
    Film getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    boolean validationFilm(Film film);

    Map<Long, Film> getFilms();

}
