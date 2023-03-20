package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final UserStorage userStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        Film film = filmStorage.getById(id);
        film.setGenres(genreService.getByFilmId(id));
        return film;
    }

    public Film add(Film film) {
        Film receivedFilm = filmStorage.add(film);
        if (film.getGenres() != null) {
            genreService.updateForFilm(receivedFilm.getId(), film.getGenres());
        }
        return receivedFilm;
    }

    public Film update(Film film) {
        if (film.getGenres() != null) {
            genreService.updateForFilm(film.getId(), film.getGenres());
        }
        return filmStorage.update(film);
    }

    public void addLike(Long id, Long userId) throws NotFoundException {
        filmStorage.getById(id);
        userStorage.getById(userId);
        filmStorage.addLike(id, userId);
    }

    public void removeLike(Long id, Long userId) throws NotFoundException {
        filmStorage.getById(id);
        userStorage.getById(userId);
        filmStorage.removeLike(id, userId);
    }

    public List<Film> getTop(Integer count) {
        return filmStorage.getTop(count);
    }
}

