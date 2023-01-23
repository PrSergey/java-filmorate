package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping("/users")
    public User addUser(@RequestBody @Valid User user) {
        log.info("Добавляем пользователя {}", user);
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(id);
        users.put(id, user);
        id++;
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody @Valid User user) {
        Long id = user.getId();
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        for (Map.Entry a : users.entrySet()) {
            if (a.getKey().equals(id)) {
                users.put(id, user);
            }
        }
        log.info("Обновляем пользователя {}", user);
        return user;
    }

    @GetMapping("/users")
    public Map<Long, User> getAllUser() {
        log.debug("Текущее колличество пользователей {}", users.size());
        return users;
    }
}
