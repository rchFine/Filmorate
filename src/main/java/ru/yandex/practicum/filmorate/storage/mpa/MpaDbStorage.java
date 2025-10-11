package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public Collection<MpaRating> getAllMpa() {
        String sql = "SELECT * FROM mpa_rating";
        log.info("Получение всех MPA рейтингов");
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs));
    }

    public MpaRating getMpaById(int id) {
        String sql = "SELECT * FROM mpa_rating WHERE id = ?";
        log.info("Получение MPA рейтинга с id {}", id);
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToMpa(rs), id);
    }

    private MpaRating mapRowToMpa(ResultSet rs) throws SQLException {
        MpaRating mpa = new MpaRating();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("mpa_value"));
        return mpa;
    }
}
