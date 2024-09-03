package rs.ac.bg.fon.reservationservice.constraints;
import rs.ac.bg.fon.reservationservice.constraints.validators.DateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRangeConstraint {

    String message() default "Start date must be before end date and not in the past";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

