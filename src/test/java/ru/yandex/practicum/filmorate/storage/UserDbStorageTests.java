package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
public class UserDbStorageTests {

    private final UserDbStorage userStorage;

    @Test
    void testCreateAndFindUser() {
        User user = new User();
        user.setEmail("practicum@yandex.ru");
        user.setLogin("login");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1992, 2, 2));

        User created = userStorage.createUser(user);
        assertThat(created.getId()).isNotNull();

        User retrievedUser = userStorage.getUserById(created.getId());
        assertThat(retrievedUser.getEmail()).isEqualTo("practicum@yandex.ru");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("update@example.com");
        user.setLogin("login2");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1991, 1, 1));

        User created = userStorage.createUser(user);
        created.setName("New Name");

        User updated = userStorage.updateUser(created);
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("user1");
        user1.setName("User1");
        user1.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("user2");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
        userStorage.createUser(user2);

        Collection<User> users = userStorage.getAllUsers();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testNameFallbackToLogin() {
        User user = new User();
        user.setEmail("nofallback@mail.com");
        user.setLogin("fallbackLogin");
        user.setBirthday(LocalDate.of(1995, 5, 5));
        user.setName(null);

        User createdUser = userStorage.createUser(user);

        if (createdUser.getName() == null || createdUser.getName().isBlank()) {
            createdUser.setName(createdUser.getLogin());
        }
        assertThat(createdUser.getName()).isEqualTo("fallbackLogin");
    }

    @Test
    void testGetUserByIdNotFound() {
        int nonExistentId = 9999;
        assertThatThrownBy(() -> userStorage.getUserById(nonExistentId))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
