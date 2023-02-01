package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;


public interface UserStorage {
    public List<User> allUsers();
    public User createUser(User user);
    public User updateUser(User user);
    public boolean validationUser(User user);
    public Map<Long, User> getUsers();
}

