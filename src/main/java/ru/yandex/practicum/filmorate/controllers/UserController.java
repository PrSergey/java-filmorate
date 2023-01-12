package ru.yandex.practicum.filmorate.controllers;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    List<User> users = new ArrayList<>();

    @PostMapping("/user")
    public User addUser(@RequestBody @Valid User user) {
        log.info("Добавляем пользователя");
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.add(user);
        return user;
    }

    @PatchMapping("/patchUser")
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Обновляем пользователя");
        IntStream.range(0, users.size()).filter(i -> users.get(i) == user).forEachOrdered(i -> users.set(i, user));
        return user;
    }

    @GetMapping("/users")
    public List<User> getAllUser() {
        log.debug("Текущее колличество пользователей {}", users.size());
        return users;
    }
}
