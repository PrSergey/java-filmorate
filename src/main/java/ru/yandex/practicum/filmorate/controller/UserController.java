package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private int id = 1;
    private List<User> users = new ArrayList<>();


    @GetMapping("/users")
    public List<User> allUsers() {
        log.debug("Получен запрос GET /users.");
        return users;
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        log.debug("Получен запрос POST /users.");
        if (checkUser(user)) {
            throw new ValidationException();
        }
        if (user.getName()==null){
            user.setName(user.getLogin());
        }
        user.setId(id++);
        users.add(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        log.debug("Получен запрос PUT /users.");
        if (checkUser(user) || (users.size()<user.getId())) {
            throw new ValidationException();
        }
        users.set((user.getId()-1), user);
        return user;
    }

    public boolean checkUser (User user) {
        return user.getLogin().contains(" ");

    }
}
