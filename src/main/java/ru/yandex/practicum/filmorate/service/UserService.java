package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    Long addFriend(Long userId, Long friendId);

    Long deleteFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getMutualFriends(Long userId, Long friendId);

    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(User user);

}
