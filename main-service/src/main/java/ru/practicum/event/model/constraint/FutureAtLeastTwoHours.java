package ru.practicum.event.model.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = FutureAtLeastTwoHoursValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureAtLeastTwoHours {
    String message() default "The date must be at least 2 hours in the future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
