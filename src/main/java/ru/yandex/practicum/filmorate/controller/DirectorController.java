package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/directors")
    public List<Director> findAll() {
        return directorService.getAll();
    }

    @GetMapping("/directors/{id}")
    public Director findById(@PathVariable Long id) {
        return directorService.getById(id);
    }

    @PostMapping("/directors")
    public Director create(@RequestBody Director director) {
        return directorService.add(director);
    }

    @PutMapping("/directors")
    public Director update(@RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/directors/{id}")
    public void delete(@PathVariable Long id) {
        directorService.delete(id);
    }

}