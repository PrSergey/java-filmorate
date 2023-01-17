package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validation.CustomValidDate;
import ru.yandex.practicum.filmorate.validation.CustomValidTime;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @CustomValidDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @CustomValidTime
    private LocalTime duration;

}
