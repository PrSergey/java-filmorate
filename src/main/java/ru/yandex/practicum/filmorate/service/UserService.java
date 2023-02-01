package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    public Long addFriend (Long userId, Long friendId);
    public Long deleteFriend (Long userId, Long friendId);
    public Set<Long> getFriends (Long userId);
    public List<Long> getMutualFriends (Long userId, Long friendId);

}
