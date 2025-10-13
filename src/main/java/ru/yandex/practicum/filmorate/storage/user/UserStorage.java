package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(int userId);

    void addFriend(int userId, int friendId, FriendStatus friendStatus);

    void updateFriendStatus(int userId, int friendId, FriendStatus friendStatus);

    void removeFriend(int userId, int friendId);
}
