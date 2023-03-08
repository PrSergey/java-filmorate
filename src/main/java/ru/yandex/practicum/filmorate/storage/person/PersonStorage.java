package ru.yandex.practicum.filmorate.storage.person;

import ru.yandex.practicum.filmorate.model.Person;

import java.util.List;
import java.util.Map;


public interface PersonStorage {
    List<Person> getAllPerson();
    Person getPersonById(Long id);

    Person createPerson(Person person);

    Person updatePerson(Person person);

    boolean validationPerson(Person person);

    Map<Long, Person> getPerson();
}

