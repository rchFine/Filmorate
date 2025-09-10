package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    void testValidUserInfo() {
        User user = new User();
        user.setEmail("practicum@yandex.ru");
        user.setLogin("practicum");
        user.setBirthday(LocalDate.of(2003, 3, 3));

        assertNotNull(user.getEmail());
        assertTrue(user.getEmail().contains("@"));
        assertNotNull(user.getLogin());
        assertFalse(user.getLogin().isBlank());
        assertNotNull(user.getBirthday());
        assertFalse(user.getBirthday().isAfter(LocalDate.now()));
    }

    @Test
    void testInvalidUserEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2005, 5, 5));

        assertFalse(user.getEmail().contains("@"), "Email без @");
    }

    @Test
    void testEmptyUserLogin() {
        User user = new User();
        user.setEmail("practicum@yandex.ru");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2010, 10, 10));

        assertTrue(user.getLogin() == null || user.getLogin().isBlank(),
                "Логин пользователя не может быть пустым");
    }

    @Test
    void testBirthdayInFuture() {
        User user = new User();
        user.setEmail("practicum@yandex.ru");
        user.setLogin("practicum");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertTrue(user.getBirthday().isAfter(LocalDate.now()),
                "Дата рождения пользователя в будущем не возможна");
    }
}
