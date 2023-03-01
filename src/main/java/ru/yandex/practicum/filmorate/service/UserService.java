package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Person;

import java.util.List;

public interface UserService {

    Long addFriend(Long userId, Long friendId);

    Long deleteFriend(Long userId, Long friendId);

    List<Person> getFriends(Long userId);

    List<Person> getMutualFriends(Long userId, Long friendId);

    List<Person> getAllUsers();

    Person getUserById(Long id);

    Person createUser(Person person);

    Person updateUser(Person person);

}
