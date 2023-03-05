package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.service.PersonService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class PersonController {

    private final PersonService userInMemory;

    @Autowired
    public PersonController(PersonService userInMemory) {
        this.userInMemory = userInMemory;
    }

    @GetMapping("/users")
    public List<Person> getAllUsers() {
        return userInMemory.getAllPerson();
    }

    @PostMapping("/users")
    public Person createPerson(@Valid @RequestBody Person person) throws ValidationException {
        return userInMemory.createPerson(person);
    }

    @PutMapping("/users")
    public Person updatePerson(@Valid @RequestBody Person person) throws ValidationException {
        return userInMemory.updatePerson(person);
    }

    @GetMapping("/users/{id}")
    public Person getPersonById(@PathVariable Long id) {
        return userInMemory.getPersonById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public Long addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (id < 0 || friendId < 0) {
            throw new ExistenceException("Id не может быть отрицательный");
        }
        return userInMemory.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public Long deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userInMemory.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<Person> getFriendsOfUser(@PathVariable Long id) {
        return userInMemory.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<Person> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userInMemory.getMutualFriends(id, otherId);
    }

}
