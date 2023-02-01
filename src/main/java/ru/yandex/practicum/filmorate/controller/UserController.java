package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UserController {

    private final UserStorage userInMemory;
    @Autowired
    public UserController(UserStorage userInMemory) {
        this.userInMemory = userInMemory;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userInMemory.allUsers();
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        return userInMemory.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userInMemory.updateUser(user);
    }

}
