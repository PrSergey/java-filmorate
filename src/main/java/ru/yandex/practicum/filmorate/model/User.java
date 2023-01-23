package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CustomValidLogin;

import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class User {

    private Long id;

    @Email
    private String email;

    @CustomValidLogin
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

}
