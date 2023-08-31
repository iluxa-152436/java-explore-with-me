package ru.practicum.explorewithme.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDateConstraint, LocalDateTime> {

    @Override
    public void initialize(EventDateConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext constraintValidatorContext) {
        return !eventDate.isBefore(LocalDateTime.now().plusHours(2));
    }
}
