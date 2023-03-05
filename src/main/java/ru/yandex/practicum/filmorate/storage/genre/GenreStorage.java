package ru.yandex.practicum.filmorate.storage.genre;

import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Set<Genre> getAll();

    Genre getById(Long id) throws NotFoundException;

    Set<Genre> getByFilmId(Long filmId) throws NotFoundException;

    void addAllToFilmId(Long filmId, Set<Genre> genre);

    void deleteAllByFilmId(Long filmId);
}
