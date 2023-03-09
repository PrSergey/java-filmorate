package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;
    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenre() {
        log.debug("Выдача всех жанров.");
        return genreStorage.getAllGenre();
    }

    public Genre getGenreById(Long id) {
        log.debug("Выдача жанра по id.");
        for (Genre genre : getAllGenre()) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        return null;
    }
}
