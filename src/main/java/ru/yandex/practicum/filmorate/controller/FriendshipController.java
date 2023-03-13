package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.service.person.FriendService;

import java.util.List;

@RestController
@Slf4j
public class FriendshipController {

    private final FriendService friendService;

    @Autowired
    public FriendshipController(FriendService friendService) {
        this.friendService = friendService;
    }


    @PutMapping("/users/{id}/friends/{friendId}")
    public Long addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (id < 0 || friendId < 0) {
            throw new ExistenceException("Id не может быть отрицательный");
        }
        return friendService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public Long deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return friendService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<Person> getFriendsOfUser(@PathVariable Long id) {
        return friendService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<Person> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return friendService.getMutualFriends(id, otherId);
    }
}
