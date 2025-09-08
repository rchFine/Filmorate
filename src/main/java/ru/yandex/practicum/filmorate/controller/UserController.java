package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);
        validateUser(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: {}", user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Запрос на обновление данных пользователя: {}", user.getId());

        if (user.getId() == null) {
            log.error("Ошибка: отсутствует id при обновлении пользователя");
            throw new ValidateException("Не указан id пользователя для обновления");
        }
        if (!users.containsKey(user.getId())) {
            log.error("Ошибка: пользователь с id {} не найден", user.getId());
            throw new ValidateException("Пользователь с id " + user.getId() + " не найден");
        }

        validateUser(user);
        users.put(user.getId(), user);
        log.info("Данные пользователя {} успешно обновлены", user.getLogin());
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


    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidateException("email не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidateException("Email должен содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidateException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidateException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("Дата рождения не может быть в будущем");
        }
    }
}
