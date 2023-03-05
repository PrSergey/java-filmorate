package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getById(Long id) throws NotFoundException {
        return genreStorage.getById(id);
    }

    public List<Genre> getByFilmId(Long filmId) throws NotFoundException {
        return genreStorage.getByFilmId(filmId);
    }

    public void updateForFilm(Long filmId, List<Genre> genres) {
        genreStorage.deleteAllByFilmId(filmId);
        genreStorage.addAllToFilmId(filmId, genres);
    }
}
