package ru.yandex.practicum.filmorate.model;

public enum FriendStatus {
    PENDING, CONFIRMED;

    public static FriendStatus fromString(String status) {
        if (status == null) return null;
        switch (status.toUpperCase()) {
            case "PENDING":
                return FriendStatus.PENDING;
            case "CONFIRMED":
                return FriendStatus.CONFIRMED;
            default:
                throw new IllegalArgumentException("Неизвестный статус дружбы: " + status);
        }
    }
}
