package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validation.CustomValidLogin;

import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import java.sql.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User {

    private Long id;

    @Email
    private String email;

    @CustomValidLogin
    private String login;

    private String name;

    @PastOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    private Set<Long> friends;
}
