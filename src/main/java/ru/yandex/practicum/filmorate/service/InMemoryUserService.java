package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class InMemoryUserService implements UserService{
    UserStorage userInMemory;

    public InMemoryUserService(UserStorage userInMemory) {
        this.userInMemory = userInMemory;
    }


    @Override
    public Long addFriend(Long userId, Long friendId) {
        if (getUserFriends(userId)==null){
            return null;
        }else{
            getUserFriends(userId).add(friendId);
            return friendId;
        }
    }

    @Override
    public Long deleteFriend (Long userId, Long friendId) {
        if (getUserFriends(userId)==null){
            return null;
        }else{
            getUserFriends(userId).remove(friendId);
            return friendId;
        }
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        if (getUserFriends(userId)==null){
            return null;
        }else{
            return getUserFriends(userId);
        }
    }

    public HashSet<Long> getUserFriends (Long userId){
        if (userInMemory.getUsers().containsKey(userId)){
            return null;
        }else{
            return userInMemory.getUsers().get(userId).getFriends();
        }
    }
}
