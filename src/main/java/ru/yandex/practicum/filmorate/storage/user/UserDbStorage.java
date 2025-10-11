package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper = (rs, rowNum) -> mapRowToUser(rs);

    @Override
    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return jdbcTemplate.query("SELECT * FROM users ORDER BY id", userMapper);
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        log.info("Получение пользователя с id {}", id);
        return jdbcTemplate.queryForObject(sql, userMapper, id);
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, user.getBirthday() != null ? java.sql.Date.valueOf(user.getBirthday()) : null);
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        log.info("Создан пользователь с id {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("ID пользователя не может быть пустым при обновлении");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        int updatedUser = jdbcTemplate.update("UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday() != null ? java.sql.Date.valueOf(user.getBirthday()) : null,
                user.getId());

        if (updatedUser == 0) {
            throw new NoSuchElementException("Пользователь с id " + user.getId() + " не найден");
        }

        log.info("Обновлен пользователь с id {}", user.getId());
        return getUserById(user.getId());
    }

    public void addFriend(int userId, int friendId, FriendStatus status) {
        String sql = "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)";
        log.info("Пользователь {} добавил в друзья пользователя {} со статусом {}", userId, friendId, status);
        jdbcTemplate.update(sql, userId, friendId, status.name());
    }

    public void updateFriendStatus(int userId, int friendId, FriendStatus status) {
        String sql = "UPDATE friendship SET status = ? WHERE user_id = ? AND friend_id = ?";
        log.info("Обновление статуса дружбы пользователя {} с пользователем {} на {}", userId, friendId, status);
        jdbcTemplate.update(sql, status.name(), userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        log.info("Пользователь {} удалил из друзей пользователя {}", friendId, userId);
        jdbcTemplate.update(sql, userId, friendId);
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        java.sql.Date bd = rs.getDate("birthday");
        user.setBirthday(bd != null ? bd.toLocalDate() : null);
        user.setFriends(getFriendsByUserId(user.getId()));
        return user;
    }

    private Map<Integer, FriendStatus> getFriendsByUserId(int userId) {
        String sql = "SELECT friend_id, status FROM friendship WHERE user_id = ?";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, FriendStatus> friends = new HashMap<>();
            while (rs.next()) {
                friends.put(rs.getInt("friend_id"), FriendStatus.fromString(rs.getString("status").toUpperCase()));
            }
            return friends;
        }, userId);
    }
}
