package ru.yandex.practicum.filmorate.controllers;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    List<Film> films = new ArrayList<>();

    @PostMapping("/film")
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Добавляем фильм");
        films.add(film);
        return film;
    }

    @PatchMapping("/patch")
    public Film updateFilm(@RequestBody @Valid Film film) {
        Long id = film.getId();
        IntStream.range(0, films.size()).filter(i -> films.get(i).getId().equals(id)).forEachOrdered(i -> films.set(i, film));
        log.info("Обновляем фильм");
        return film;
    }

    @GetMapping("/films")
    public List<Film> getAllFilm() {
        log.debug("Текущее колличество фильмов {}", films.size());
        return films;
    }
}
