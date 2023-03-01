package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;

import java.util.*;

@Data
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;
    private Map<Long, Person> users = new HashMap<>();

    public List<Person> allUsers() {
        log.debug("Выдача всех пользователей из хранилища.");
        return new ArrayList<>(users.values());
    }

    public Person createUser(Person person) throws ValidationException {
        log.debug("Создание пользователя в хранилище.");
        if (validationUser(person)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (person.getName() == null || person.getName().isBlank()) {
            person.setName(person.getLogin());
        }
        person.setFriends(new HashSet<>());
        person.setId(id++);
        users.put(person.getId(), person);
        return person;
    }

    public Person updateUser(Person person) throws ValidationException {
        log.debug("Обновление пользователя из хранилище.");
        if (validationUser(person)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (!users.containsKey(person.getId())) {
            throw new ExistenceException("Пользователь с id " + person.getId() + " не найден.");
        }
        if (person.getFriends() == null) {
            person.setFriends(new HashSet<>());
        }
        users.put(person.getId(), person);
        return person;
    }

    public boolean validationUser(Person person) {
        log.debug("Валидация логина пользователя на пробел.");
        return person.getLogin().contains(" ");

    }

    public Map<Long, Person> getUsers() {
        return users;
    }
}
