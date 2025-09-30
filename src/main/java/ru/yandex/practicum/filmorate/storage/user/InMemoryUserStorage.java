package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new NoSuchElementException("Пользователь с id " + user.getId() + " не найден");
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    private int generateId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
