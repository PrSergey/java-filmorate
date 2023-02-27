# java-filmorate
Template repository for Filmorate project.
## ER - диграмма
![ER-filmorate](https://user-images.githubusercontent.com/112753342/221633496-cc25b310-22b9-4b3a-b0e1-60980a0bcc29.png)
## Примеры запросов
### Получение всех пользоватей:
```
SELECT * 
FROM user
```
### Получение пользователя по id:
```
SELECT *
FROM user
WHERE user_id=1
```
### Получение друзей пользователя:
```
SELECT friend_id
FROM friendship
WHERE user_id=2;
```
### Получение общих друзей пользователей:
```
SELECT friend_id
FROM friendship
WHERE user_id=1
AND friend_id IN (SELECT friend_id 
FROM friendship
WHERE user_id = 2
AND confirmation IS TRUE)
AND confirmation IS TRUE;
```
### Получение всех фильмов:
```
SELECT * 
FROM movie;
```
### Получение фильмов по id:
```
SELECT * 
FROM movie
WHERE film_id=1;
```
### Получение 10 популярных фильмов:
```
SELECT film_id,
COUNT(user_id)
FROM likes
ORDER COUNT(user_id) DESC
LIMIT (10);
```




