package ru.explorewithme.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDateConstraint {
    String message() default "Event date must be valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
