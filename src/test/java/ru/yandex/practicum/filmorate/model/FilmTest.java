package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {
    @Test
    void testValidFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(1995, 2, 12));
        film.setDuration(166);

        assertNotNull(film.getName());
        assertFalse(film.getName().isBlank());
        assertNotNull(film.getDescription());
        assertTrue(!film.getDescription().isEmpty() && film.getDescription().length() <= 200);
        assertNotNull(film.getReleaseDate());
        assertTrue(film.getDuration() >= 1);
    }

    @Test
    void testBlankFilmName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(1899, 1, 1));
        film.setDuration(120);

        assertTrue(film.getName() == null || film.getName().isBlank(),
                "Имя фильма не может быть пустым!");
    }

    @Test
    void testTooLongDescription() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(1924, 8, 11));
        film.setDuration(120);

        assertFalse(!film.getDescription().isEmpty() && film.getDescription().length() <= 200,
                "Описание фильма не может превышать 200 символов!");
    }

    @Test
    void testReleaseDateNull() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(null);
        film.setDuration(120);

        assertNull(film.getReleaseDate(), "Поле даты выхода фильма не может быть пустым!");
    }

    @Test
    void testDurationInvalid() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        assertTrue(film.getDuration() < 1, "Длительность фильма не может быть меньше минуты");
    }
}
