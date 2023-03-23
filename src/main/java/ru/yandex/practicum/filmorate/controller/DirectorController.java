package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> findAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable Long id) {
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@RequestBody @Valid Director director) {
        return directorService.add(director);
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        directorService.delete(id);
    }

}