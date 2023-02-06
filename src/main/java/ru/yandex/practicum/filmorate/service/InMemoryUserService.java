package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InMemoryUserService implements UserService {
    UserStorage userInMemory;

    @Autowired
    public InMemoryUserService(UserStorage userInMemory) {
        this.userInMemory = userInMemory;
    }

    @Override
    public List<User> getAllUsers() {
        return userInMemory.allUsers();
    }

    @Override
    public User getUserById(Long id) {
        if (!userInMemory.getUsers().containsKey(id)){
            throw new ValidationException("Пользователь с id " + id + " не найден");
        }
        return userInMemory.getUsers().get(id);
    }

    @Override
    public User createUser(User user) {
        return userInMemory.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userInMemory.updateUser(user);
    }

    @Override
    public Long addFriend(Long userId, Long friendId) {
        getUserFriends(userId).add(friendId);
        getUserFriends(friendId).add(userId);
        return friendId;

    }

    @Override
    public Long deleteFriend(Long userId, Long friendId) {
        if (!getUserFriends(userId).contains(friendId)) {
            throw new ValidationException("У пользователя с id " + userId + " нет друга с id " + friendId);
        } else {
            getUserFriends(userId).remove(friendId);
            getUserFriends(friendId).remove(userId);
            return friendId;
        }
    }

    @Override
    public List<User> getFriends(Long userId) throws ValidationException {
        List<User> userFriends = new ArrayList<>();
        for (Long friend: getUserById(userId).getFriends()){
            userFriends.add(getUserById(friend));
        }
        return userFriends;
    }

    @Override
    public List<Long> getMutualFriends(Long userId, Long friendId) throws ValidationException {
        List<Long> mutualFriends = new ArrayList<>();
        Set<Long> friendsOfUser = getUserFriends(userId);
        Set<Long> friendsOfUserFriend = getUserFriends(friendId);
        if (friendsOfUser == null){
            return mutualFriends;
        }
        for (Long friends : friendsOfUser) {
            if (friendsOfUserFriend.contains(friends)) {
                mutualFriends.add(friends);
            }
        }
        return mutualFriends;
    }

    public Set<Long> getUserFriends(Long userId) throws ValidationException {
        if (!userInMemory.getUsers().containsKey(userId)) {
            throw new ValidationException("Пользователь c id" + userId + " не найден в базе");
        } else {
            return userInMemory.getUsers().get(userId).getFriends();
        }
    }
}
