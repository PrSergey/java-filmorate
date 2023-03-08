package ru.yandex.practicum.filmorate.storage.person;

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
public class InMemoryPersonStorage implements PersonStorage {
    private int id = 1;
    private Map<Long, Person> users = new HashMap<>();

    public List<Person> getAllPerson() {
        log.debug("Выдача всех пользователей из хранилища.");
        return new ArrayList<>(users.values());
    }

    public void addFriends(Long user_id, Long friend_id) {
        users.get(user_id).getFriends().add(getPersonById(friend_id));
    }


    public boolean deleteFriend(Long user_id, Long friend_id) {
        return getPersonById(user_id).getFriends().remove(getPersonById(friend_id));
    }

    @Override
    public Person getPersonById(Long id) {
        log.debug("Выдача пользователя по id.");
        if (!users.containsKey(id)) {
            throw new ExistenceException("Пользователь с id " + id + " не найден.");
        }
        return users.get(id);
    }

    public Person createPerson(Person person) throws ValidationException {
        log.debug("Создание пользователя в хранилище.");
        if (validationPerson(person)) {
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

    public Person updatePerson(Person person) throws ValidationException {
        log.debug("Обновление пользователя из хранилище.");
        if (validationPerson(person)) {
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

    public boolean validationPerson(Person person) {
        log.debug("Валидация логина пользователя на пробел.");
        return person.getLogin().contains(" ");
    }

    public Map<Long, Person> getPerson() {
        return users;
    }
}
