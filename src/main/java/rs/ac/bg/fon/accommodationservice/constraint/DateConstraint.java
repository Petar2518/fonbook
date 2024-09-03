package rs.ac.bg.fon.accommodationservice.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import rs.ac.bg.fon.accommodationservice.constraint.validator.DateValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateConstraint {


    String message() default "Date From field needs to be before Date To field";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
