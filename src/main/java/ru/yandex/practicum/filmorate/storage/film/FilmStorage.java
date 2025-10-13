package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    List<Film> getMostPopular(int count);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Set<Genre> getGenresByFilmId(int filmId);

}
