package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;


public interface FilmStorage {
    public List<Film> allFilms();
    public Film createFilm(Film film);
    public Film updateFilm(Film film);
    public boolean validationFilm(Film film);
    public Map<Long, Film> getFilms();
}
