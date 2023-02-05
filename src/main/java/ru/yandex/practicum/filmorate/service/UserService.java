package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(getUser(id));
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public List<User> getUserFriends(Long id) {
        return getUser(id).getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public void addFriends(Long userId, Long friendId) {
        User user1 = getUser(userId);
        User user2 = getUser(friendId);

        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
    }

    public void deleteFriends(Long userId, Long friendId) {
        User user1 = getUser(userId);
        User user2 = getUser(friendId);

        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
    }

    public List<User> getCommonFriendList(Long userId, Long friendId) {
        User user1 = getUser(userId);
        User user2 = getUser(friendId);

        Set<Long> commonFriends = new HashSet<>(user1.getFriends());
        commonFriends.retainAll(user2.getFriends());

        return commonFriends.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

}
