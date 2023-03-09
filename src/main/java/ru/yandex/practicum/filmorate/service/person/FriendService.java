package ru.yandex.practicum.filmorate.service.person;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.storage.person.DbFriendStorage;
import ru.yandex.practicum.filmorate.storage.person.DbPersonStorage;
import ru.yandex.practicum.filmorate.storage.person.PersonStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FriendService {

    private final PersonStorage personStorage;
    private final DbFriendStorage dbFriendStorage;


    @Autowired
    public FriendService(@Qualifier("dbPersonStorage")PersonStorage personStorage, DbFriendStorage dbFriendStorage) {
        this.personStorage = personStorage;
        this.dbFriendStorage = dbFriendStorage;
    }

    public Long addFriend(Long userId, Long friendId) {
        log.debug("Добавление друга в сервисе.");
        dbFriendStorage.addFriends(userId, friendId);
        return friendId;
    }

    public Long deleteFriend(Long userId, Long friendId) {
        log.debug("Удаление друга в сервисе.");
        if (!getFriends(userId).contains(personStorage.getPersonById(friendId))) {
            throw new ExistenceException("У пользователя с id " + userId + " нет друга с id " + friendId);
        }
        if (dbFriendStorage.deleteFriend(userId,friendId)) {
            return friendId;
        } else {
            throw new ExistenceException("У пользователя с id " + userId + " нет друга с id " + friendId);
        }
    }

    public List<Person> getFriends(Long userId) throws ValidationException {
        log.debug("Выдача друзей в сервисе.");
        return new ArrayList<>(personStorage.getPersonById(userId).getFriends());
    }

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
        if (!personStorage.getPerson().containsKey(userId)) {
            throw new ExistenceException("Пользователь c id" + userId + " не найден в базе");
        } else {
            return personStorage.getPersonById(userId).getFriends();
        }
    }
}
