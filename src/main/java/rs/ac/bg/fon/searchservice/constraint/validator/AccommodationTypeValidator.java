package rs.ac.bg.fon.searchservice.constraint.validator;

import rs.ac.bg.fon.searchservice.constraint.AccommodationTypeConstraint;
import rs.ac.bg.fon.searchservice.model.AccommodationType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.EnumUtils;

public class AccommodationTypeValidator implements ConstraintValidator<AccommodationTypeConstraint, String> {
    @Override
    public void initialize(AccommodationTypeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String type, ConstraintValidatorContext constraintValidatorContext) {

        if (type != null) {
            return EnumUtils.isValidEnum(AccommodationType.class, type) || type.equals("");
        }

        return true;
    }
}
