package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping("/films")
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Добавляем фильм{}", film);
        filmService.addFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film film) {
        filmService.updateFilm(film);
        log.info("Обновляем фильм {}", film);
        return film;
    }

    @GetMapping("/films")
    public List<Film> getAllFilm() {
        log.debug("Текущее колличество фильмов {}", filmService.getAllFilms().size());
        return new ArrayList<>(filmService.getAllFilms());
    }

    @DeleteMapping("/films")
    public void deleteFilm(@RequestBody Long id) {
        log.info("Фильм {} удален", id);
        filmService.deleteFilm(id);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilm(@RequestParam(required = false) Long count) {
        return new ArrayList<>(filmService.getPopularFilms(count));
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
        filmService.addLikes(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} удалил лайк у фильма {}", userId, id);
        filmService.deleteLike(id, userId);
    }

}
