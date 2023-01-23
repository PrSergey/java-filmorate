package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class FilmController {

    private Long id = 1L;
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping("/films")
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Добавляем фильм{}", film);
        film.setId(id);
        films.put(id, film);
        id++;
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film film) {
        Long id = film.getId();
        for (Map.Entry a : films.entrySet()) {
            if (a.getKey().equals(id)) {
                films.put(id, film);
            }
        }
        log.info("Обновляем фильм {}", film);
        return film;
    }

    @GetMapping("/films")
    public Map<Long, Film> getAllFilm() {
        log.debug("Текущее колличество фильмов {}", films.size());
        return films;
    }
}
