package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.service.person.PersonService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class PersonController {

    @Qualifier("dbPersonStorage")
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/users")
    public List<Person> getAllUsers() {
        return personService.getAllPerson();
    }

    @PostMapping("/users")
    public Person createPerson(@Valid @RequestBody Person person) throws ValidationException {
        return personService.createPerson(person);
    }

    @PutMapping("/users")
    public Person updatePerson(@Valid @RequestBody Person person) throws ValidationException {
        return personService.updatePerson(person);
    }

    @GetMapping("/users/{id}")
    public Person getPersonById(@PathVariable Long id) {
        return personService.getPersonById(id);
    }

}
