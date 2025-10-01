package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().put(friendId, FriendStatus.PENDING);
        friend.getFriends().put(userId, FriendStatus.PENDING);
        return user;
    }

    public User removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().put(friendId, FriendStatus.CONFIRMED);
        friend.getFriends().put(userId, FriendStatus.CONFIRMED);

        return user;
    }

    public List<User> getFriends(int userId) {
        return userStorage.getUserById(userId).getFriends().keySet().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends().keySet();
        Set<Integer> otherFriends = userStorage.getUserById(otherId).getFriends().keySet();

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::getUserById)
                .toList();
    }
}
