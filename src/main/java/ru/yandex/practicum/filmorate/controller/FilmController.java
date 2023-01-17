package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FilmController {
    private final List<Film> films = new ArrayList<>();

    @PostMapping("/film")
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Добавляем фильм{}", film);
        films.add(film);
        return film;
    }

    @PutMapping("/patch")
    public Film updateFilm(@RequestBody @Valid Film film) {
        Long id = film.getId();
        IntStream.range(0, films.size()).filter(i -> films.get(i).getId().equals(id)).forEachOrdered(i -> films.set(i, film));
        log.info("Обновляем фильм {}", film);
        return film;
    }

    @GetMapping("/films")
    public List<Film> getAllFilm() {
        log.debug("Текущее колличество фильмов {}", films.size());
        return films;
    }
}
