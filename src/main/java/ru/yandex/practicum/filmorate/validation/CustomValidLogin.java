package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {CheckValidLogin.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank
public @interface CustomValidLogin {

    String message() default "Логин не должен быть пустым и содержать пробелов";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
