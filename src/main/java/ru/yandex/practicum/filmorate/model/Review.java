package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.stream.DoubleStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Review {
    @Positive
    private Long id;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long filmId;
    @NotNull
    private Long userId;
    private Integer useful;

}
