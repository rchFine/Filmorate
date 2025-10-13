DELETE FROM friendship;
DELETE FROM likes;
DELETE FROM film_genres;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM mpa_rating;

ALTER TABLE mpa_rating ALTER COLUMN id RESTART WITH 1;
ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN id RESTART WITH 1;

INSERT INTO mpa_rating (mpa_value) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');

INSERT INTO genres (name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');


INSERT INTO users (name, email, login, birthday) VALUES
('Mr. Drew Anderson', 'Karley85@yahoo.com', 'lrEX80Kmx9', '1995-11-18'),
('Jane Doe', 'jane.doe@example.com', 'jane123', '1996-05-10');

INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES
('Фильм 1', 'Описание 1', '2011-11-02', 120, 1),
('Фильм 2', 'Описание 2', '2012-12-02', 120, 2),
('Фильм 3', 'Описание 3', '2013-03-03', 120, 3),
('Фильм 4', 'Описание 4', '2014-04-04', 120, 4),
('Фильм 5', 'Описание 5', '2015-05-05', 120, 5);

INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

INSERT INTO likes (user_id, film_id) VALUES
(1, 1),
(2, 2);

INSERT INTO friendship (user_id, friend_id, status) VALUES
(1, 2, 'PENDING');