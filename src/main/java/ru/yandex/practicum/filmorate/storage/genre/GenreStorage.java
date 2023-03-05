package ru.yandex.practicum.filmorate.storage.genre;

import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAll();

    Genre getById(Long id) throws NotFoundException;

    List<Genre> getByFilmId(Long filmId) throws NotFoundException;

    void addAllToFilmId(Long filmId, List<Genre> genre);

    void deleteAllByFilmId(Long filmId);
}
