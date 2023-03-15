package ru.yandex.practicum.filmorate.storage.film;

import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    List<Film> getAll();

    Film getById(Long id) throws NotFoundException;

    Film add(Film film);

    Set<Long> getAllLikes(Long id);

    Film update(Film film);

    void deleteFilmById(Long filmId);

    void deleteFilmById(Long filmId);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    boolean hasLikeFromUser(Long id, Long userId);

    List<Film> getTop(Integer count);

}
