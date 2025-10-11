package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(MpaDbStorage.class)
class MpaDbStorageTests {

    private final MpaDbStorage mpaStorage;

    @Test
    void testGetMpaById() {
        MpaRating mpa = mpaStorage.getMpaById(1);
        assertThat(mpa).isNotNull();
        assertThat(mpa.getName()).isNotBlank();
    }

    @Test
    void testGetNonExistentMpa() {
        assertThatThrownBy(() -> mpaStorage.getMpaById(999))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
