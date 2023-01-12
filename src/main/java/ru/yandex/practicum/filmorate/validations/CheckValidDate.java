package ru.yandex.practicum.filmorate.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class CheckValidDate implements ConstraintValidator<CustomValidDate, LocalDate> {

    @Override
    public void initialize(CustomValidDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(LocalDate.of(1985, 12, 28));
    }
}
