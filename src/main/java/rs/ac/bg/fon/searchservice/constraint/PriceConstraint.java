package rs.ac.bg.fon.searchservice.constraint;

import rs.ac.bg.fon.searchservice.constraint.validator.PriceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PriceValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PriceConstraint {

    String message() default "Minimum price must be less than maximum price";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
