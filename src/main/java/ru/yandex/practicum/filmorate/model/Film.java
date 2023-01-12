package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.validations.CustomValidDate;
import ru.yandex.practicum.filmorate.validations.CustomValidTime;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Component
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size(min = 1, max = 200)
    private String description;

    @CustomValidDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @CustomValidTime
    private LocalTime duration;

}
