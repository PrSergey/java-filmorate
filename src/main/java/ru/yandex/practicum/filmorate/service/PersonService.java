package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Person;

import java.util.List;

public interface PersonService {

    Long addFriend(Long userId, Long friendId);

    Long deleteFriend(Long userId, Long friendId);

    List<Person> getFriends(Long userId);

    List<Person> getMutualFriends(Long userId, Long friendId);

    List<Person> getAllPerson();

    Person getPersonById(Long id);

    Person createPerson(Person person);

    Person updatePerson(Person person);

}
