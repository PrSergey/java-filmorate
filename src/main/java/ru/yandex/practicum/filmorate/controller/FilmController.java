package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.constant.SortType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Добавляем фильм{}", film);
        filmService.add(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Обновляем фильм {}", film);
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> getAllFilm() {
        log.debug("Текущее колличество фильмов {}", filmService.getAll().size());
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@RequestParam(required = false, defaultValue = "10") Integer count,
                                     @RequestParam(required = false, defaultValue = "0") Long genreId,
                                     @RequestParam(required = false, defaultValue = "0") Integer year) {
        return filmService.getPopularWithGenreAndYear(count, genreId, year);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} удалил лайк у фильма {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) {
        log.info("Фильм с id = {} удален", filmId);
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirectorId(@PathVariable Long directorId, @RequestParam SortType sortBy) {
        return filmService.getFilmsByDirectorId(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilm(@RequestParam(value = "userId", required = false) Long userId,
                                    @RequestParam(value = "friendId", required = false) Long friendId) {
        return filmService.getCommonFilm(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam(value = "query", required = false) String query,
                                  @RequestParam(value = "by", required = false) String by) {
        return filmService.searchFilms(query, by);
    }
}
