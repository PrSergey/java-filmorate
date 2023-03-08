package ru.yandex.practicum.filmorate.service.person;

import ru.yandex.practicum.filmorate.model.Person;

import java.util.List;

public interface PersonService {


    List<Person> getAllPerson();

    Person getPersonById(Long id);

    Person createPerson(Person person);

    Person updatePerson(Person person);

}
