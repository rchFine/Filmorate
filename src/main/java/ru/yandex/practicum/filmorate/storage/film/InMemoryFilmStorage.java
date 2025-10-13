package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
@Profile("test")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new LinkedHashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new NoSuchElementException("Id фильма обязателен для обновления");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NoSuchElementException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.getLikes().add(userId);
        films.put(filmId, film);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.getLikes().remove(userId);
        films.put(filmId, film);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed()
                        .thenComparingInt(Film::getId))
                .limit(count)
                .toList();
    }

    @Override
    public Set<Genre> getGenresByFilmId(int filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NoSuchElementException("Фильм с id " + filmId + " не найден");
        }
        return new LinkedHashSet<>(film.getGenres());
    }

    private int generateId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}