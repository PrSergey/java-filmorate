package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.storage.PersonStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class PersonServiceImp implements PersonService {
    PersonStorage personInMemory;

    @Autowired
    public PersonServiceImp(PersonStorage personInMemory) {
        this.personInMemory = personInMemory;
    }

    @Override
    public List<Person> getAllPerson() {
        log.debug("Выдача всех пользователей из сервиса.");
        return personInMemory.getAllPerson();
    }

    @Override
    public Person getPersonById(Long id) {
        log.debug("Выдача пользователя из сервиса.");
        if (personInMemory.getPersonById(id)==null) {
            throw new ExistenceException("Пользователь с id " + id + " не найден");
        }
        return personInMemory.getPersonById(id);
    }

    @Override
    public Person createPerson(Person person) {
        log.debug("Создание пользователя в сервисе.");
        return personInMemory.createPerson(person);
    }

    @Override
    public Person updatePerson(Person person) {
        log.debug("Обновление пользователя в сервисе.");
        return personInMemory.updatePerson(person);
    }

    @Override
    public Long addFriend(Long userId, Long friendId) {
        log.debug("Добавление друга в сервисе.");
        personInMemory.addFriends(userId, friendId);
        return friendId;
    }

    @Override
    public Long deleteFriend(Long userId, Long friendId) {
        log.debug("Удаление друга в сервисе.");
        if (!getFriends(userId).contains(getPersonById(friendId))) {
            throw new ExistenceException("У пользователя с id " + userId + " нет друга с id " + friendId);
        }
        if (personInMemory.deleteFriend(userId,friendId)) {
            return friendId;
        } else {
            throw new ExistenceException("У пользователя с id " + userId + " нет друга с id " + friendId);
        }
    }

    @Override
    public List<Person> getFriends(Long userId) throws ValidationException {
        log.debug("Выдача друзей в сервисе.");
        List<Person> personFriends = new ArrayList<>();
        for (Person friend : getPersonById(userId).getFriends()) {
            personFriends.add(friend);
        }
        return personFriends;
    }

    @Override
    public List<Person> getMutualFriends(Long userId, Long friendId) throws ValidationException {
        log.debug("Выдача общих друзей в сервисе.");
        List<Person> mutualFriends = new ArrayList<>();
        if (getPersonFriends(userId) == null) {
            return mutualFriends;
        }
        for (Person friends : getPersonFriends(userId)) {
            if (getPersonFriends(friendId).contains(friends)) {
                mutualFriends.add(friends);
            }
        }

        return mutualFriends;
    }

    public Set<Person> getPersonFriends(Long userId) throws ValidationException {
        log.debug("Выдача списка друзей пользователя.");
        if (!personInMemory.getPerson().containsKey(userId)) {
            throw new ExistenceException("Пользователь c id" + userId + " не найден в базе");
        } else {
            return personInMemory.getPersonById(userId).getFriends();
        }
    }
}
