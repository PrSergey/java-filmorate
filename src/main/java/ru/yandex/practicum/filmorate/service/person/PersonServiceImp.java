package ru.yandex.practicum.filmorate.service.person;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.storage.person.DbFriendStorage;
import ru.yandex.practicum.filmorate.storage.person.PersonStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class PersonServiceImp implements PersonService {
    PersonStorage personStorage;

    @Autowired
    public PersonServiceImp(@Qualifier("dbPersonStorage")PersonStorage personStorage) {
        this.personStorage = personStorage;
    }

    @Override
    public List<Person> getAllPerson() {
        log.debug("Выдача всех пользователей из сервиса.");
        return personStorage.getAllPerson();
    }

    @Override
    public Person getPersonById(Long id) {
        log.debug("Выдача пользователя из сервиса.");
        if (personStorage.getPersonById(id)==null) {
            throw new ExistenceException("Пользователь с id " + id + " не найден");
        }
        return personStorage.getPersonById(id);
    }

    @Override
    public Person createPerson(Person person) {
        log.debug("Создание пользователя в сервисе.");
        return personStorage.createPerson(person);
    }

    @Override
    public Person updatePerson(Person person) {
        log.debug("Обновление пользователя в сервисе.");
        return personStorage.updatePerson(person);
    }

}
