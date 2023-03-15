package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        System.out.printf("Тест");
        SpringApplication.run(FilmorateApplication.class, args);
    }

}