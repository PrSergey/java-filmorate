package ru.yandex.practicum.filmorate.service;

public interface FilmService {

    public Long addLike (Long filmId, Long userId);
    public Long deleteLike (Long filmId, Long userId);
    public Long getTopTenFilmsOfLikes ();

}
