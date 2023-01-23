package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UserController {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private int id = 1;
    private Map <Long, User> users = new HashMap<>();


    @GetMapping("/users")
    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        if (validationUser(user)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (validationUser(user)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (!users.containsKey(user.getId())){
            throw new ValidationException("Пользователь с id "+user.getId()+" не найден.");
        }
        users.put(user.getId(), user);
        return user;
    }

    public boolean validationUser(User user) {
        return user.getLogin().contains(" ");

    }
}
