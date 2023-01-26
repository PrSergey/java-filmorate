package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private Long id = 1L;
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@RequestBody @Valid Film film) {
        log.info("Добавляем фильм{}", film);
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        return ResponseEntity.ok(film);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновляем фильм {}", film);
        } else {
            log.warn("Такого ID нет");
            return ResponseEntity.internalServerError().body(film);
        }
        return ResponseEntity.ok(film);
    }

    @GetMapping("/films")
    public ResponseEntity<List<Film>> getAllFilm() {
        log.debug("Текущее колличество фильмов {}", films.size());
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }
}
