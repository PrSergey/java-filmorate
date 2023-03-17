package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.time.LocalDate;

public class CheckValidDate implements ConstraintValidator<CustomValidDate, Date> {

    @Override
    public void initialize(CustomValidDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Date localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate != null) {
            return localDate.toLocalDate().isAfter(LocalDate.of(1895, 12, 28));
        }
        return false;
    }
}
