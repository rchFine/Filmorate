package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Запрос на добавление фильма: {}", film);
        validateFilm(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм {} успешно добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Запрос на обновлении информации о фильме с id {}", film.getId());

        if (film.getId() == null) {
            log.error("Ошибка: не указан id фильма");
            throw new ValidateException("Id фильма обязателен для обновления");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Ошибка: фильм с id {} не найден", film.getId());
            throw new ValidateException("Фильм c id " + film.getId() + " не найден");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Фильм {} успешно обновлён", film.getName());
        return film;
    }

    private int generateId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidateException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidateException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidateException("Продолжительность фильма должна быть положительной");
        }
    }
}
