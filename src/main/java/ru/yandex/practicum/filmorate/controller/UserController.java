package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UserController {

    private final UserService userInMemory;
    @Autowired
    public UserController(UserService userInMemory) {
        this.userInMemory = userInMemory;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userInMemory.getAllUsers();
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        return userInMemory.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userInMemory.updateUser(user);
    }

    @GetMapping ("/users/{id}")
    public User getUserById (@PathVariable Long id){
        return userInMemory.getUserById(id);
    }
    @PutMapping ("/users/{id}/friends/{friendId}")
    public Long addFriend (@PathVariable Long id, @PathVariable Long friendId){
        if (id<0){
            throw new ValidationException("id не может быть отрицательным");
        }
        return userInMemory.addFriend(id, friendId);
    }

    @DeleteMapping ("/users/{id}/friends/{friendId}")
    public Long deleteFriend (@PathVariable Long id, @PathVariable Long friendId){
        return userInMemory.deleteFriend(id, friendId);
    }

    @GetMapping ("/users/{id}/friends")
    public List<User> getFriendsOfUser (@PathVariable Long id){
        return userInMemory.getFriends(id);
    }

    @GetMapping ("/users/{id}/friends/common/{otherId}")
    public List<Long> getMutualFriends(@PathVariable Long id, @PathVariable  Long otherId){
        return userInMemory.getMutualFriends(id, otherId);
    }

}
