# java-filmorate
Template repository for Filmorate project.
## ER - диграмма
![er-диаграмма]![image](https://user-images.githubusercontent.com/112753342/224692796-18fdee1a-8d1a-4e60-bd66-549bd706bd96.png)
## Примеры запросов
### Получение всех пользоватей:
```
SELECT * 
FROM person
```
### Получение пользователя по id:
```
SELEC *
FROM person
WHERE person_id=1
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
FROM film;
```
### Получение фильмов по id:
```
SELECT * 
FROM film
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




