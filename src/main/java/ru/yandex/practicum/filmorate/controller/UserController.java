package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public User addUser(@RequestBody @Valid User user) {
        log.info("Добавляем пользователя {}", user);
        userService.add(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody @Valid User user) {
        userService.update(user);
        log.info("Обновляем пользователя {}", user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getAllUser() {
        log.debug("Текущее колличество пользователей {}", userService.getAll().size());
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        List<User> one = userService.getAllFriends(id);
        List<User> two = userService.getAllFriends(otherId);
        return one.stream().filter(two::contains).collect(Collectors.toList());
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getAllFriends(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    void addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь {} добавил пользователя {} в друзья", id, friendId);
        userService.makeFriends(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    void deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь {} удалил пользователя {} из друзей", id, friendId);
        userService.removeFriends(id, friendId);
    }

}
