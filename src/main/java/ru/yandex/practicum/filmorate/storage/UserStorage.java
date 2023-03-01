package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Person;

import java.util.List;
import java.util.Map;


public interface UserStorage {
    List<Person> allUsers();

    Person createUser(Person person);

    Person updateUser(Person person);

    boolean validationUser(Person person);

    Map<Long, Person> getUsers();
}

