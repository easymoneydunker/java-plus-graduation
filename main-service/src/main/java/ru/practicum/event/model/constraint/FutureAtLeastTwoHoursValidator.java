package ru.practicum.event.model.constraint;

import java.time.LocalDateTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FutureAtLeastTwoHoursValidator implements ConstraintValidator<FutureAtLeastTwoHours, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.isAfter(LocalDateTime.now().plusHours(2));
    }
}
