package ru.yandex.practicum.filmorate.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalTime;

public class CheckValidTime implements ConstraintValidator<CustomValidTime, LocalTime> {

    @Override
    public void initialize(CustomValidTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalTime localTime, ConstraintValidatorContext constraintValidatorContext) {
        return localTime.isAfter(LocalTime.of(0,0,0));
    }
}
