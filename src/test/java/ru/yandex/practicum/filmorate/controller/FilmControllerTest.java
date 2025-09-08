package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldThrowWhenNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        assertThrows(ValidateException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("A".repeat(201));
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        assertThrows(ValidateException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowWhenReleaseDateTooEarly() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Normal description");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThrows(ValidateException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldThrowWhenDurationNotPositive() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Normal");
        film.setDuration(0);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        assertThrows(ValidateException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldCreateFilmWhenValid() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Valid description");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        Film created = filmController.createFilm(film);
        assertNotNull(created.getId());
        assertEquals("Test", created.getName());
    }
}
