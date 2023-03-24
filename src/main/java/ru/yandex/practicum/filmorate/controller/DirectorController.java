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
        log.info("Запрос на получение всех рижессеров");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable Long id) {
        log.info("Запрос на получение рижессера с id= {}", id);
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@RequestBody @Valid Director director) {
        log.info("Создание рижессера с именем {}", director.getName());
        return directorService.add(director);
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        log.info("Обновление рижессера с id= {}", director.getId());
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Удаление рижессера с id= {}", id);
        directorService.delete(id);
    }

}