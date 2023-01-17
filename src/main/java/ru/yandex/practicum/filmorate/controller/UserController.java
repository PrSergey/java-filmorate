package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final List<User> users = new ArrayList<>();

    @PostMapping("/user")
    public User addUser(@RequestBody @Valid User user) {
        log.info("Добавляем пользователя {}", user);
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.add(user);
        return user;
    }

    @PutMapping("/patchUser")
    public User updateUser(@RequestBody @Valid User user) {

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            Long id = user.getId();
            IntStream.range(0, users.size()).filter(i -> users.get(i).getId().equals(id)).forEachOrdered(i -> users.set(i, user));
            user.setName(user.getLogin());
        }
        log.info("Обновляем пользователя {}", user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getAllUser() {
        log.debug("Текущее колличество пользователей {}", users.size());
        return users;
    }
}
