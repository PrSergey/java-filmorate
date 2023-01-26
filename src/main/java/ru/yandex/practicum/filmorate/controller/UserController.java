package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        log.info("Добавляем пользователя {}", user);
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(id);
        users.put(user.getId(), user);
        id++;
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody @Valid User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновляем пользователя {}", user);
        } else {
            log.warn("Такого ID нет");
            return ResponseEntity.internalServerError().body(user);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        log.debug("Текущее колличество пользователей {}", users.size());
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }
}
