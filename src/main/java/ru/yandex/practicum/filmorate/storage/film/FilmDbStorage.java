package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final UserDbStorage userStorage;
    private final RowMapper<Film> filmMapper = (rs, rowNum) -> mapRowToFilm(rs);

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films ORDER BY id";
        log.info("Получение всех фильмов из базы");
        return jdbcTemplate.query(sql, filmMapper);
    }

    @Override
    public Film getFilmById(int id) {
        try {
            log.info("Получение фильма с id {}", id);
            return jdbcTemplate.queryForObject("SELECT * FROM films WHERE id = ?", filmMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Фильм с id {} не найден", id);
            throw new NoSuchElementException("Фильм с id= " + id + " не найден");
        }
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getMpa() == null || mpaDbStorage.getMpaById(film.getMpa().getId()) == null) {
            log.warn("Некорректный id MPA для фильма {}", film);
            throw new IllegalArgumentException("Некорректный id MPA");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa() != null ? film.getMpa().getId() : 1);
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        updateFilmGenres(film.getId(), film.getGenres());
        film.setGenres(getGenresByFilmId(film.getId()));

        log.info("Создан фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update("UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_rating_id=? WHERE id=?",
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : 1,
                film.getId());

        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }

        updateFilmGenres(film.getId(), film.getGenres());

        log.info("Обновлён фильм с id {}:", film.getId());
        return getFilmById(film.getId());
    }

    private void updateFilmGenres(int filmId, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
            }
        }
    }

    private Set<Genre> getGenresByFilmId(int filmId) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT g.id, g.name FROM genres g " +
                        "JOIN film_genres fg ON g.id = fg.genre_id " +
                        "WHERE fg.film_id = ? " +
                        "ORDER BY g.id",
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getInt("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }, filmId);
        return new LinkedHashSet<>(genres);
    }

    public List<Film> getMostPopular(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY like_count DESC, f.id ASC " +
                "LIMIT ?";

        log.info("Получение {} самых популярных фильмов", count);
        return jdbcTemplate.query(sql, filmMapper, count);
    }

    public Film addLike(int filmId, int userId) {
        getFilmById(filmId);
        userStorage.getUserById(userId);

        String sql = "MERGE INTO likes (user_id, film_id) KEY(user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);

        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
        return getFilmById(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        getFilmById(filmId);
        userStorage.getUserById(userId);

        jdbcTemplate.update("DELETE FROM likes WHERE user_id = ? AND film_id = ?", userId, filmId);

        log.info("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
        return getFilmById(filmId);
    }

    private Set<Integer> getLikesByFilmId(int filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setLikes(getLikesByFilmId(film.getId()));

        film.setMpa(mpaDbStorage.getMpaById(rs.getInt("mpa_rating_id")));
        film.setGenres(getGenresByFilmId(film.getId()));
        return film;
    }
}

