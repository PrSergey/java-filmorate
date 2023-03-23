package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.EventUser;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.info("Добавляем пользователя {}", user);
        userService.add(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        userService.update(user);
        log.info("Обновляем пользователя {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAllUser() {
        log.debug("Текущее колличество пользователей {}", userService.getAll().size());
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Выдача общих фильмов");
        List<User> one = userService.getAllFriends(id);
        List<User> two = userService.getAllFriends(otherId);
        return one.stream().filter(two::contains).collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getAllFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь {} добавил пользователя {} в друзья", id, friendId);
        userService.makeFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь {} удалил пользователя {} из друзей", id, friendId);
        userService.removeFriends(id, friendId);
    }

    @GetMapping("/{id}/feed")
    public List<EventUser> getEventFeed(@PathVariable Long id) {
        getUser(id);
        return userService.getEventFeed(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Удален пользователь id = {}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        log.info("Получение рекомендаций для пользователя {}", id);
        return userService.getRecommendations(id);
    }


}
