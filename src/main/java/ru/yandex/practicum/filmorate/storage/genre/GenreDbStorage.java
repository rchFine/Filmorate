package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public Collection<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        log.info("Получение всех жанров");
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToGenre(rs));
    }

    public Genre getGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToGenre(rs), id);

    }

    private Genre mapRowToGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
