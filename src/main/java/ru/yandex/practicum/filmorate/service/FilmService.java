package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final @Qualifier("filmDbStorage") FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaDbStorage mpaStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);

        if (film == null) {
            throw new NoSuchElementException("Фильм с id " + id + " не найден");
        }

        film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        film.setGenres(filmStorage.getGenresByFilmId(film.getId()));

        return film;
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public void addLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        filmStorage.addLike(filmId, userId);
    }


    public void removeLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getMostPopular(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getMpa() == null || film.getMpa().getId() <= 0 || film.getGenres() == null) {
            throw new ValidateException("Некорректный идентификатор MPA или жанра");
        }
    }
}
