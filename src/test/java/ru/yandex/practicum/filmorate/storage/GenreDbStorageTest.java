package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(GenreDbStorage.class)
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    void testGetGenreById() {
        Genre genre = genreStorage.getGenreById(1);
        assertThat(genre).isNotNull();
        assertThat(genre.getName()).isNotBlank();
    }

    @Test
    void testGetNonExistentGenre() {
        assertThatThrownBy(() -> genreStorage.getGenreById(999))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
