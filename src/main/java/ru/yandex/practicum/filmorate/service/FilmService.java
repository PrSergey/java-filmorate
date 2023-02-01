package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    public Long addLike (Long filmId, Long userId);
    public Long deleteLike (Long filmId, Long userId);
    public Long getTopTenFilmsOfLikes ();
    public Film addFilm (Film film);
    public Film updateFilm (Film film);
    public List<Film> getFilms ();


}
