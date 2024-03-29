package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.constant.EventOperation;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.SortType;
import ru.yandex.practicum.filmorate.model.EventUser;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.EventFeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final UserStorage userStorage;
    private final DirectorService directorService;
    private final DirectorStorage directorStorage;

    private final EventFeedStorage eventFeedStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        Film film = filmStorage.getById(id);
        film.setGenres(genreService.getByFilmId(id));
        film.setDirectors(directorStorage.getByFilmId(id));
        return film;
    }

    public Film add(Film film) {
        Film receivedFilm = filmStorage.add(film);
        if (film.getGenres() != null) {
            genreService.updateForFilm(receivedFilm.getId(), film.getGenres());
        }
        if (film.getDirectors() != null) {
            directorService.updateForFilm(receivedFilm.getId(), film.getDirectors());
        }
        return receivedFilm;
    }

    public void deleteFilmById(Long filmId){
        filmStorage.deleteFilmById(filmId);
    }

    public Film update(Film film) {
        if (film.getGenres() != null) {
            genreService.updateForFilm(film.getId(), film.getGenres());
        }
        if (film.getDirectors() != null) {
            directorService.updateForFilm(film.getId(), film.getDirectors());
        }
        if (film.getDirectors() == null) {
            directorStorage.deleteAllByFilmId(film.getId());
        }
        Film filmAfterUpdate = filmStorage.update(film);
        filmAfterUpdate.setGenres(genreService.getByFilmId(filmAfterUpdate.getId()));
        filmAfterUpdate.setDirectors(directorStorage.getByFilmId(filmAfterUpdate.getId()));
        return filmAfterUpdate;
    }

    public void addLike(Long id, Long userId) throws NotFoundException {
        EventUser eventUser = new EventUser(userId, id, EventType.LIKE, EventOperation.ADD);
        eventFeedStorage.setEventFeed(eventUser);
        filmStorage.getById(id);
        userStorage.getById(userId);
        filmStorage.addLike(id, userId);
    }

    public void removeLike(Long id, Long userId) throws NotFoundException {
        filmStorage.getById(id);
        userStorage.getById(userId);
        filmStorage.removeLike(id, userId);
        EventUser eventUser = new EventUser(userId, id, EventType.LIKE, EventOperation.REMOVE);
        eventFeedStorage.setEventFeed(eventUser);
    }


    public List<Film> getTop(Integer count) {
        return filmStorage.getTop(count);
    }

    public List<Film> getFilmsByDirectorId(Long directorId, SortType sortBy) throws NotFoundException {
        directorService.getById(directorId);
        return filmStorage.getFilmsByDirectorId(directorId, sortBy);
    }

    public List<Film> getCommonFilm (Long userId, Long friendId){
        return filmStorage.getCommonFilm(userId, friendId);
    }


    public List<Film> searchFilms(String query, String by) {
        List<String> splitBy = new ArrayList<>();
        if (by.contains(",")) {
            splitBy.addAll(Arrays.asList(by.toLowerCase().split(",")));
        } else {
            splitBy.add(by.toLowerCase());
        }
        return filmStorage.searchFilms(query.toLowerCase(), splitBy);
    }

    public List<Film> getPopularWithGenreAndYear(Integer count, Long genreId, Integer year) {
        return filmStorage.getPopularWithGenreAndYear(count, genreId, year);
    }
}

