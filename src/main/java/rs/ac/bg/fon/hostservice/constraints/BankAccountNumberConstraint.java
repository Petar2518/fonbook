package rs.ac.bg.fon.hostservice.constraints;

import rs.ac.bg.fon.hostservice.constraints.validator.BankAccountNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {BankAccountNumberValidator.class})
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BankAccountNumberConstraint {

    String message() default "Invalid bank account";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
