package ru.practicum.dto.event.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class FutureAtLeastTwoHoursValidator implements ConstraintValidator<FutureAtLeastTwoHours, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.isAfter(LocalDateTime.now().plusHours(2));
    }
}
