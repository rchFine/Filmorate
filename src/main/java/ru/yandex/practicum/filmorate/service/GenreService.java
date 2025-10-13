package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreStorage;

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        Genre genre = genreStorage.getGenreById(id);
        if (genre == null) {
            throw new NoSuchElementException("Жанр с id " + id + " не найден");
        }
        return genreStorage.getGenreById(id);
    }
}
