package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@Positive @PathVariable int id) {
        log.info("Запрос на получение пользователя с id {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление данных пользователя: {}", user.getId());
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@Positive @PathVariable int id, @Positive @PathVariable int friendId) {
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public User confirmFriend(@Positive @PathVariable int id, @Positive @PathVariable int friendId) {
        log.info("Пользователь {} подтвердил дружбу с {}", id, friendId);
        return userService.confirmFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@Positive @PathVariable int id, @Positive @PathVariable int friendId) {
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@Positive @PathVariable int id) {
        log.info("Запрос на получение списка друзей пользователя с id {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@Positive @PathVariable int id, @Positive @PathVariable int otherId) {
        log.info("Запрос на получение общих друзей пользователей с id {} и id {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
