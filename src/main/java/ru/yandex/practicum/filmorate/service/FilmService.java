package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NoSuchElementException("Фильм с таким id не найден: " + filmId);
        }
        if (userStorage.getUserById(userId) != null) {
            film.getLikes().add(userId);
        }
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (userStorage.getUserById(userId) == null) {
            throw new NoSuchElementException("Пользователь с id " + userId + " не найден");
        }

        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
