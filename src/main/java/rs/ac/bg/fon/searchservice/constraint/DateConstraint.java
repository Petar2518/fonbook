package rs.ac.bg.fon.searchservice.constraint;

import rs.ac.bg.fon.searchservice.constraint.validator.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateConstraint {


    String message() default "CheckOut date must be after checkIn date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
