package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.GenreService;

import java.util.List;

@RestController
@Slf4j
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenre() {
        return genreService.getAllGenre();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        if (getAllGenre().size() < id || id < 0) {
            throw new ExistenceException("Данного жанра не существует.");
        }
        return genreService.getGenreById(id);
    }
}
