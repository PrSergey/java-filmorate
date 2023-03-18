package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director getById(Long id) throws NotFoundException {
        return directorStorage.getById(id);
    }

    public List<Director> getByFilmId(Long filmId) throws NotFoundException {
        return directorStorage.getByFilmId(filmId);
    }

    public Director add(Director director) {
        return directorStorage.add(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public void delete(Long id) {
        directorStorage.delete(id);
    }

    public void updateForFilm(Long filmId, List<Director> directors) {
        directorStorage.deleteAllByFilmId(filmId);
        if (directors != null) {
            directorStorage.addAllToFilmId(filmId, directors);
        }
    }

}
