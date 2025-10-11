package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaStorage;

    public Collection<MpaRating> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public MpaRating getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }
}
