package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final Comparator<Film> filmLikesComparator = Comparator.comparingInt(film -> film.getLikes().size());
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteFilm(getFilm(id));
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    public void addLikes(Long id, Long userId) {
        getFilm(id).getLikes().add(userService.getUser(userId).getId());
    }

    public void deleteLike(Long id, Long userId) {
        getFilm(id).getLikes().remove(userService.getUser(userId).getId());
    }

    public List<Film> getPopularFilms(Long count) {
        return filmStorage.getAllFilms().stream()
                .sorted(filmLikesComparator.reversed())
                .limit(count == null ? 10 : count)
                .collect(Collectors.toList());
    }

}

