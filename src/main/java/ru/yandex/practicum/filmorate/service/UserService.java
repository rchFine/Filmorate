package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final @Qualifier("userDbStorage") UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public User getUserById(int userId) {
        User user = userStorage.getUserById(userId);

        if (user == null) {
            throw new NoSuchElementException("Пользователь с id " + userId + " не найден");
        }

        return user;
    }

    public User addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends().containsKey(friendId)) {
            throw new ValidateException("Заявка дружбы уже была отправлена");
        }

        user.getFriends().put(friendId, FriendStatus.PENDING);
        userStorage.addFriend(userId, friendId, FriendStatus.PENDING);
        return user;
    }

    public User confirmFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        FriendStatus status = friend.getFriends().get(userId);
        if (status == null || status != FriendStatus.PENDING) {
            throw new FriendNotFoundException("Нельзя подтвердить дружбу - заявка на дружбу отсутствует");
        }

        user.getFriends().put(friendId, FriendStatus.CONFIRMED);
        friend.getFriends().put(userId, FriendStatus.CONFIRMED);

        userStorage.updateFriendStatus(userId, friendId, FriendStatus.CONFIRMED);
        userStorage.updateFriendStatus(friendId, userId, FriendStatus.CONFIRMED);

        return user;
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = getUserById(userId);
        return user.getFriends().keySet().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUserById(userId);
        User other = getUserById(otherId);

        Set<Integer> userFriendIds = user.getFriends().keySet();
        Set<Integer> otherFriendIds = other.getFriends().keySet();

        return userFriendIds.stream()
                .filter(otherFriendIds::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
