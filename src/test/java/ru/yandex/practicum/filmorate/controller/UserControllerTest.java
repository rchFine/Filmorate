package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void shouldThrowWhenEmailIsEmpty() {
        User user = new User();
        user.setEmail("");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidateException ex = assertThrows(ValidateException.class, () -> userController.createUser(user));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    void shouldThrowWhenEmailWithoutMailSymbol() {
        User user = new User();
        user.setEmail("invalid.email");
        user.setLogin("testLogin");

        assertThrows(ValidateException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("bad login");
        assertThrows(ValidateException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldUseLoginAsNameWhenNameEmpty() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testLogin");
        user.setName("");

        User created = userController.createUser(user);
        assertEquals("testLogin", created.getName());
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidateException.class, () -> userController.createUser(user));
    }
}
