package ru.yandex.practicum.filmorate.storage.director;

import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    List<Director> getAll();

    Director getById(Long id) throws NotFoundException;

    List<Director> getByFilmId(Long filmId) throws NotFoundException;

    Director add(Director director);

    Director update(Director director);

    void delete(Long id);

    void addAllToFilmId(Long filmId, List<Director> directors);

    void deleteAllByFilmId(Long filmId);

}
