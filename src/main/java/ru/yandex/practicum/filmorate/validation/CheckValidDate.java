package ru.yandex.practicum.filmorate.validation;

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
        if (localDate != null) {
            return localDate.isAfter(LocalDate.of(1895, 12, 28));
        }
        return false;
    }
}
