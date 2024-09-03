package rs.ac.bg.fon.searchservice.constraint;

import rs.ac.bg.fon.searchservice.constraint.validator.AccommodationTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AccommodationTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccommodationTypeConstraint {


    String message() default "Accommodation type is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
