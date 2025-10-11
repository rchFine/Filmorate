
package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class, MpaDbStorage.class, GenreDbStorage.class})
class FilmDbStorageTests {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    private Film createTestFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);

        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");

        film.setGenres(Set.of(genre1, genre2));

        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        return filmStorage.createFilm(film);
    }

    private User createTestUser(String login, String email) {
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 10, 10));
        return userStorage.createUser(user);
    }

    @Test
    void testCreateAndGetFilm() {
        Film film = createTestFilm("Film1");
        Film created = filmStorage.createFilm(film);
        Film retrieved = filmStorage.getFilmById(created.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("Film1");
        assertThat(retrieved.getMpa()).isNotNull();
        assertThat(retrieved.getMpa().getId()).isEqualTo(film.getMpa().getId());

        assertThat(retrieved.getGenres()).isEmpty();
        assertThat(retrieved.getLikes()).isEmpty();
    }

    @Test
    void testUpdateFilm() {
        Film film = createTestFilm("Film2");
        film.setName("Updated Name");
        film.setDuration(150);

        Film updated = filmStorage.updateFilm(film);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getDuration()).isEqualTo(150);
    }

    @Test
    void testGetAllFilms() {
        createTestFilm("Film3");
        createTestFilm("Film4");

        List<Film> films = (List<Film>) filmStorage.getAllFilms();
        films = films.stream().filter(f -> f.getName().startsWith("Film")).toList();

        assertThat(films).hasSize(2);
    }

    @Test
    void testAddAndRemoveLike() {
        User user = createTestUser("user1", "user1@yandex.ru");
        Film film = createTestFilm("Film5");

        filmStorage.addLike(film.getId(), user.getId());
        assertThat(filmStorage.getFilmById(film.getId()).getLikes()).contains(user.getId());

        filmStorage.removeLike(film.getId(), user.getId());
        assertThat(filmStorage.getFilmById(film.getId()).getLikes()).doesNotContain(user.getId());
    }

    @Test
    void testGetMostPopular() {
        User user1 = createTestUser("user1", "user1@yandex.ru");
        User user2 = createTestUser("user2", "user2@yandex.ru");
        User user3 = createTestUser("user3", "user3@yandex.ru");

        Film film1 = createTestFilm("Film6");
        Film film2 = createTestFilm("Film7");

        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film1.getId(), user2.getId());
        filmStorage.addLike(film2.getId(), user3.getId());

        List<Film> popular = filmStorage.getMostPopular(5).stream()
                .filter(f -> f.getName().startsWith("Film"))
                .limit(2)
                .toList();

        assertThat(popular)
                .hasSize(2)
                .extracting(Film::getName)
                .containsExactly("Film6", "Film7");

        assertThat(popular.get(0).getLikes().size())
                .isGreaterThanOrEqualTo(popular.get(1).getLikes().size());
    }

    @Test
    void testGetFilmByIdNotFound() {
        assertThatThrownBy(() -> filmStorage.getFilmById(999))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("не найден");
    }
}


