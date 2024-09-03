package rs.ac.bg.fon.hostservice.constraints.validator;

import rs.ac.bg.fon.hostservice.constraints.BankAccountNumberConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BankAccountNumberValidator implements ConstraintValidator<BankAccountNumberConstraint, String> {
    @Override
    public void initialize(BankAccountNumberConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String field, ConstraintValidatorContext constraintValidatorContext) {
        return field.matches("[0-9]+")
                && (field.length() > 8) && (field.length() <= 17);
    }
}
