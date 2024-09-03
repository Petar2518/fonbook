package rs.ac.bg.fon.searchservice.constraint.validator;

import rs.ac.bg.fon.searchservice.constraint.DateConstraint;
import rs.ac.bg.fon.searchservice.dto.SearchRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<DateConstraint, SearchRequest> {
    @Override
    public void initialize(DateConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SearchRequest searchRequest, ConstraintValidatorContext constraintValidatorContext) {

        if (searchRequest.getCheckIn() != null && searchRequest.getCheckOut() != null)
            return searchRequest.getCheckOut().isAfter(searchRequest.getCheckIn()) && searchRequest.getCheckIn().isAfter(LocalDate.now());

        return searchRequest.getCheckIn() == null && searchRequest.getCheckOut() == null;
    }

}
